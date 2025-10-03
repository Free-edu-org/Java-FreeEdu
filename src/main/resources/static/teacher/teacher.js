// /js/teacher.js
import { toast, escapeHtml } from '/js/common.js';

const $  = (sel, root=document) => root.querySelector(sel);
const $$ = (sel, root=document) => [...root.querySelectorAll(sel)];
const teacherIdParam = () => `teacherId=${encodeURIComponent(state.teacherId)}`;

// ====== STATE ======
const state = {
    me: null,
    teacherId: null,
    profile: null,
    classes: null,
    subjects: null,
};

// ====== UTILS ======
async function getJson(url){
    const r = await fetch(url, { credentials: 'include' });
    if(!r.ok) throw new Error(await r.text().catch(()=>r.status));
    return r.json();
}
async function sendJson(url, method='POST', data){
    const r = await fetch(url, {
        method,
        credentials: 'include',
        headers: { 'Content-Type':'application/json' },
        body: data != null ? JSON.stringify(data) : undefined
    });
    if(!r.ok) throw new Error(await r.text().catch(()=>r.status));
    return r.headers.get('content-type')?.includes('application/json') ? r.json() : {};
}
function mountTemplate(id){
    const tpl = document.getElementById(id);
    const view = document.getElementById('view');
    view.innerHTML = '';
    view.appendChild(tpl.content.cloneNode(true));
}
function fillSelect(sel, items, getVal, getLabel, withEmpty=false){
    const opts = (items||[]).map(it=>`<option value="${getVal(it)}">${getLabel(it)}</option>`).join('');
    sel.innerHTML = withEmpty ? `<option value="" selected disabled>— wybierz —</option>${opts}` : opts;
}
function fullName(obj){
    const first = obj?.firstname ?? obj?.firstName ?? '';
    const last  = obj?.lastname  ?? obj?.lastName  ?? '';
    return `${first} ${last}`.trim();
}
const debounce = (fn, ms=300) => {
    let t; return (...args)=>{ clearTimeout(t); t=setTimeout(()=>fn(...args), ms); };
};

// ====== ROUTER ======
const routes = {
    '#schedule'  : renderSchedule,
    '#remarks'   : renderRemarks,
    '#grades'    : renderGrades,
    '#attendance': renderAttendance,
};
function handleHash(){
    const hash = location.hash || '#schedule';
    (routes[hash] || routes['#schedule'])();
}
window.addEventListener('hashchange', handleHash);

// ====== VIEWS ======
async function renderSchedule(){
    mountTemplate('tpl-schedule');
    $('#teacherBadgeSchedule').textContent = fullName(state.profile ?? state.me) || 'Nauczyciel';

    const tbody = $('#scheduleTbody');
    tbody.innerHTML = `<tr><td colspan="4" class="text-secondary">Ładowanie...</td></tr>`;

    try{
        const data = await getJson(`/api/teacher/schedule?${teacherIdParam()}`);
        if(!Array.isArray(data) || !data.length){
            tbody.innerHTML = `<tr><td colspan="4" class="text-secondary text-center">Brak danych</td></tr>`;
            return;
        }
        tbody.innerHTML = data.map(i=>{
            const teacher = (i.teacherName ?? `${i.teacherFirstName ?? ''} ${i.teacherLastName ?? ''}`)?.trim() || '';
            return `<tr>
        <td>${escapeHtml(i.date ?? '')}</td>
        <td>${escapeHtml(i.subjectName ?? i.subject ?? '')}</td>
        <td>${escapeHtml(i.className ?? '')}</td>
        <td>${escapeHtml(teacher)}</td>
      </tr>`;
        }).join('');
    }catch(e){
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="4" class="text-danger text-center">Błąd pobierania planu</td></tr>`;
    }
}

async function renderRemarks(){
    mountTemplate('tpl-remarks');

    const filter = $('#remarksFilter');
    const addBtn = $('#remarkAddBtn');
    const tbody  = $('#remarksTbody');

    // Modal
    const modalEl = document.getElementById('modalRemark');
    const modal   = new bootstrap.Modal(modalEl);
    const titleEl = document.getElementById('remarkModalTitle');
    const idEl    = document.getElementById('remarkId');
    const classEl = document.getElementById('remarkClass');
    const studEl  = document.getElementById('remarkStudent');
    const contEl  = document.getElementById('remarkContent');
    const saveBtn = document.getElementById('remarkSaveBtn');

    let data = [];

    async function ensureClasses(){
        if(!state.classes){
            state.classes = await getJson(`/api/teacher/classes?${teacherIdParam()}`);
        }
        fillSelect(
            classEl,
            state.classes,
            c=> (c.schoolClassId ?? c.id ?? c.classId),
            c=> (c.name ?? `Klasa ${c.schoolClassId ?? c.id ?? ''}`),
            true
        );
    }

    async function loadStudentsForSelectedClass(){
        const cid = classEl.value ? Number(classEl.value) : null;
        if(!cid){ studEl.innerHTML = ''; return; }
        const studs = await getJson(`/api/teacher/students?${teacherIdParam()}&classId=${encodeURIComponent(cid)}`);
        fillSelect(
            studEl,
            studs,
            s=> (s.userId ?? s.id),
            s=> `${s.firstname ?? s.firstName ?? ''} ${s.lastname ?? s.lastName ?? ''}`
        );
    }
    classEl.addEventListener('change', loadStudentsForSelectedClass);

    async function load(){
        tbody.innerHTML = `<tr><td colspan="4" class="text-secondary">Ładowanie...</td></tr>`;
        try{
            data = await getJson(`/api/teacher/remarks?${teacherIdParam()}`);
        }catch(e){
            console.error(e);
            tbody.innerHTML = `<tr><td colspan="4" class="text-danger text-center">Błąd pobierania uwag</td></tr>`;
            return;
        }
        draw();
    }

    function draw(){
        const q = (filter.value||'').toLowerCase();
        const rows = (data||[])
            .filter(r => `${r.studentFirstName} ${r.studentLastName} ${r.content}`.toLowerCase().includes(q))
            .map(r=>`
        <tr data-id="${r.id ?? r.remarkId}">
          <td>${escapeHtml(`${r.studentFirstName ?? ''} ${r.studentLastName ?? ''}`.trim())}</td>
          <td class="text-truncate" style="max-width:420px">${escapeHtml(r.content ?? '')}</td>
          <td>${escapeHtml(r.addDate ?? '')}</td>
          <td class="text-nowrap">
            <button class="btn btn-sm btn-outline-secondary me-1" data-action="edit"><i class="bi bi-pencil"></i></button>
            <button class="btn btn-sm btn-outline-danger" data-action="del"><i class="bi bi-trash"></i></button>
          </td>
        </tr>`).join('');
        tbody.innerHTML = rows || `<tr><td colspan="4" class="text-center text-secondary">Brak uwag</td></tr>`;
    }

    async function openModal(mode, id=null){
        await ensureClasses();
        contEl.value = '';
        idEl.value = '';
        classEl.value = '';
        studEl.innerHTML = '';
        if(mode === 'add'){
            titleEl.textContent = 'Dodaj uwagę';
        }else{
            titleEl.textContent = 'Edytuj uwagę';
            const r = data.find(x=> String(x.id ?? x.remarkId) === String(id));
            idEl.value = r?.id ?? r?.remarkId ?? '';
            contEl.value = r?.content ?? '';
            const sid = r?.studentId;
            if(sid){
                const label = `${r.studentFirstName ?? ''} ${r.studentLastName ?? ''}`.trim() || `Uczeń ${sid}`;
                studEl.innerHTML = `<option value="${sid}">${escapeHtml(label)}</option>`;
                studEl.value = String(sid);
            }
        }
        modal.show();
    }

    async function delRow(id){
        try{
            await sendJson(`/api/teacher/remarks/${id}?${teacherIdParam()}`, 'DELETE');
            toast('Usunięto uwagę');
            await load();
        }catch(e){
            console.error(e);
            toast('Błąd usuwania','error');
        }
    }

    saveBtn.addEventListener('click', async ()=>{
        const payload = {
            content: (contEl.value || '').trim(),
            studentId: studEl.value ? Number(studEl.value) : 0,
        };
        if(!payload.content || !payload.studentId){
            toast('Uzupełnij ucznia i treść','error'); return;
        }
        try{
            const id = idEl.value;
            if(id){
                await sendJson(`/api/teacher/remarks/${id}?${teacherIdParam()}`, 'PUT', payload);
                toast('Zapisano uwagę');
            }else{
                await sendJson(`/api/teacher/remarks?${teacherIdParam()}`, 'POST', payload);
                toast('Dodano uwagę');
            }
            modal.hide();
            await load();
        }catch(e){
            console.error(e);
            toast('Błąd zapisu','error');
        }
    });

    tbody.addEventListener('click', (e)=>{
        const btn = e.target.closest('button[data-action]');
        if(!btn) return;
        const tr = btn.closest('tr');
        const id = tr?.dataset.id;
        if(btn.dataset.action === 'edit') openModal('edit', id);
        if(btn.dataset.action === 'del')  delRow(id);
    });

    addBtn.addEventListener('click', ()=> openModal('add'));
    filter.addEventListener('input', debounce(draw, 200));
    await load();
}

async function renderGrades(){
    mountTemplate('tpl-grades');

    const addBtn = $('#gradeAddBtn');
    const tbody  = $('#gradesTbody');

    // Modal
    const modalEl = document.getElementById('modalGrade');
    const modal   = new bootstrap.Modal(modalEl);
    const titleEl = document.getElementById('gradeModalTitle');
    const idEl    = document.getElementById('gradeId');
    const subjEl  = document.getElementById('gradeSubject');
    const valEl   = document.getElementById('gradeValue');
    const classEl = document.getElementById('gradeClass');
    const studEl  = document.getElementById('gradeStudent');
    const saveBtn = document.getElementById('gradeSaveBtn');

    let data = [];

    async function ensureSubjects(){
        if(!state.subjects){
            state.subjects = await getJson(`/api/teacher/subjects?${teacherIdParam()}`);
        }
        fillSelect(subjEl, state.subjects, s=>s, s=>s, true);
    }
    async function ensureClasses(){
        if(!state.classes){
            state.classes = await getJson(`/api/teacher/classes?${teacherIdParam()}`);
        }
        fillSelect(
            classEl,
            state.classes,
            c=> (c.schoolClassId ?? c.id ?? c.classId),
            c=> (c.name ?? `Klasa ${c.schoolClassId ?? c.id ?? ''}`),
            true
        );
    }
    async function loadStudentsForClass(){
        const cid = classEl.value ? Number(classEl.value) : null;
        if(!cid){ studEl.innerHTML=''; return; }
        const studs = await getJson(`/api/teacher/students?${teacherIdParam()}&classId=${encodeURIComponent(cid)}`);
        fillSelect(
            studEl,
            studs,
            s=> (s.userId ?? s.id),
            s=> `${s.firstname ?? s.firstName ?? ''} ${s.lastname ?? s.lastName ?? ''}`
        );
    }
    classEl.addEventListener('change', loadStudentsForClass);

    async function load(){
        tbody.innerHTML = `<tr><td colspan="5" class="text-secondary">Ładowanie...</td></tr>`;
        try{
            data = await getJson(`/api/teacher/grades?${teacherIdParam()}`);
        }catch(e){
            console.error(e);
            tbody.innerHTML = `<tr><td colspan="5" class="text-danger text-center">Błąd pobierania ocen</td></tr>`;
            return;
        }
        draw();
    }

    function draw(){
        const rows = (data||[]).map(g=>{
            const id = g.gradeId ?? g.id;
            const name = `${g.studentFirstName ?? ''} ${g.studentLastName ?? ''}`.trim();
            const val = g.value != null ? Number(g.value).toFixed(g.value % 1 === 0 ? 0 : 1) : '';
            const subject = g.subject ?? g.subjectEnum ?? '';
            const date = g.gradeDate ?? '';
            return `<tr data-id="${id}"
                  data-student-id="${g.studentId ?? ''}"
                  data-student-name="${escapeHtml(name)}"
                  data-subject="${subject}"
                  data-value="${g.value ?? ''}">
        <td>${escapeHtml(name)}</td>
        <td>${escapeHtml(subject)}</td>
        <td>${escapeHtml(val)}</td>
        <td>${escapeHtml(date)}</td>
        <td class="text-nowrap">
          <button class="btn btn-sm btn-outline-secondary me-1" data-action="edit"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-outline-danger" data-action="del"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`;
        }).join('');
        tbody.innerHTML = rows || `<tr><td colspan="5" class="text-center text-secondary">Brak ocen</td></tr>`;
    }

    async function openModal(mode, tr=null){
        await ensureSubjects();
        await ensureClasses();

        idEl.value = '';
        subjEl.value = '';
        valEl.value = '';
        classEl.value = '';
        studEl.innerHTML = '';

        if(mode === 'add'){
            titleEl.textContent = 'Dodaj ocenę';
        }else{
            titleEl.textContent = 'Edytuj ocenę';
            const id = tr?.dataset.id;
            idEl.value = id || '';
            const subject = tr?.dataset.subject || '';
            const value = tr?.dataset.value || '';
            const sid   = tr?.dataset.studentId || '';
            const sname = tr?.dataset.studentName || '';

            subjEl.value = subject;
            valEl.value  = value;

            if(sid){
                studEl.innerHTML = `<option value="${sid}">${sname}</option>`;
                studEl.value = sid;
            }
        }
        modal.show();
    }

    async function delRow(id){
        try{
            await sendJson(`/api/teacher/grades/${id}?${teacherIdParam()}`, 'DELETE');
            toast('Usunięto ocenę');
            await load();
        }catch(e){
            console.error(e);
            toast('Błąd usuwania','error');
        }
    }

    saveBtn.addEventListener('click', async ()=>{
        const payload = {
            subjectEnum: subjEl.value || null,
            value: (valEl.value ? Number(valEl.value) : null),
            studentId: (studEl.value ? Number(studEl.value) : null),
        };
        if(!payload.subjectEnum || payload.value == null || !payload.studentId){
            toast('Uzupełnij przedmiot, ucznia i wartość','error'); return;
        }
        if(payload.value < 1 || payload.value > 6){
            toast('Ocena 1–6','error'); return;
        }

        try{
            const id = idEl.value;
            if(id){
                await sendJson(`/api/teacher/grades/${id}?${teacherIdParam()}`, 'PUT', payload);
                toast('Zapisano ocenę');
            }else{
                await sendJson(`/api/teacher/grades?${teacherIdParam()}`, 'POST', payload);
                toast('Dodano ocenę');
            }
            modal.hide();
            await load();
        }catch(e){
            console.error(e);
            toast('Błąd zapisu','error');
        }
    });

    // Delegacja zdarzeń
    tbody.addEventListener('click', (e)=>{
        const btn = e.target.closest('button[data-action]');
        if(!btn) return;
        const tr = btn.closest('tr');
        if(btn.dataset.action === 'edit') openModal('edit', tr);
        if(btn.dataset.action === 'del')  delRow(tr.dataset.id);
    });

    addBtn.addEventListener('click', ()=> openModal('add'));
    await load();
}

async function renderAttendance(){
    mountTemplate('tpl-attendance');

    const classEl = $('#attClass');
    const subjEl  = $('#attSubject');
    const loadBtn = $('#attLoadBtn');
    const saveBtn = $('#attSaveBtn');
    const tbody   = $('#attendanceTbody');

    async function ensureClasses(){
        if(!state.classes){
            state.classes = await getJson(`/api/teacher/classes?${teacherIdParam()}`);
        }
        fillSelect(
            classEl,
            state.classes,
            c=> (c.schoolClassId ?? c.id ?? c.classId),
            c=> (c.name ?? `Klasa ${c.schoolClassId ?? c.id ?? ''}`),
            true
        );
    }
    async function ensureSubjects(){
        if(!state.subjects){
            state.subjects = await getJson(`/api/teacher/subjects?${teacherIdParam()}`);
        }
        fillSelect(subjEl, state.subjects, s=>s, s=>s, true);
    }

    const STATUSES = ['PRESENT','ABSENT','LATE','EXCUSED'];

    function renderStudents(studs){
        if(!Array.isArray(studs) || !studs.length){
            tbody.innerHTML = `<tr><td colspan="2" class="text-secondary text-center">Brak uczniów w tej klasie</td></tr>`;
            saveBtn.disabled = true;
            return;
        }
        tbody.innerHTML = studs.map(s=>{
            const id = s.userId ?? s.id;
            const name = `${s.firstname ?? s.firstName ?? ''} ${s.lastname ?? s.lastName ?? ''}`.trim();
            const options = STATUSES.map(st=>`<option value="${st}">${st}</option>`).join('');
            return `<tr data-id="${id}">
        <td>${escapeHtml(name)}</td>
        <td>
          <select class="form-select form-select-sm att-status" style="max-width:220px">
            ${options}
          </select>
        </td>
      </tr>`;
        }).join('');
        saveBtn.disabled = false;
    }

    loadBtn.addEventListener('click', async ()=>{
        const cid = classEl.value ? Number(classEl.value) : null;
        const sub = subjEl.value || null;
        if(!cid || !sub){ toast('Wybierz klasę i przedmiot','error'); return; }
        tbody.innerHTML = `<tr><td colspan="2" class="text-secondary">Ładowanie...</td></tr>`;
        try{
            const studs = await getJson(`/api/teacher/students?${teacherIdParam()}&classId=${encodeURIComponent(cid)}`);
            renderStudents(studs);
        }catch(e){
            console.error(e);
            tbody.innerHTML = `<tr><td colspan="2" class="text-danger text-center">Błąd pobierania listy uczniów</td></tr>`;
        }
    });

    saveBtn.addEventListener('click', async ()=>{
        const sub = subjEl.value || null;
        if(!sub){ toast('Wybierz przedmiot','error'); return; }

        const map = {};
        $$('tr[data-id]', tbody).forEach(tr=>{
            const sid = Number(tr.dataset.id);
            const st  = $('.att-status', tr)?.value || 'PRESENT';
            map[sid] = st;
        });
        if(Object.keys(map).length === 0){
            toast('Brak danych do zapisania','error'); return;
        }

        try{
            await sendJson(`/api/teacher/attendance/mark?${teacherIdParam()}`, 'POST', {
                attendanceMap: map,
                globalSubject: sub
            });
            toast('Zapisano frekwencję');
        }catch(e){
            console.error(e);
            toast('Błąd zapisu frekwencji','error');
        }
    });

    await Promise.all([ensureClasses(), ensureSubjects()]);
}

// ====== INIT ======
async function init(){
    try{
        const me = await getJson('/api/auth/me');
        if(!me?.userId){ location.href='/#login'; return; }
        state.me = me;
        state.teacherId = me.userId;

        try{
            state.profile = await getJson(`/api/teacher/profile?${teacherIdParam()}`);
        }catch{
            state.profile = null;
        }

        handleHash();
    }catch(e){
        console.error(e);
        const view = document.getElementById('view');
        if(view) view.innerHTML = `<div class="card-glass p-4 text-danger">Błąd inicjalizacji panelu nauczyciela.</div>`;
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => { init().catch(console.error); });
} else {
    init().catch(console.error);
}
