// /admin/admin.js
import { toast, escapeHtml } from '../js/common.js';

const $  = (sel, root=document) => root.querySelector(sel);
const $$ = (sel, root=document) => [...root.querySelectorAll(sel)];

async function getJson(url){
    const r = await fetch(url, { credentials:'include' });
    if(!r.ok) throw new Error(await r.text().catch(()=>r.status));
    return r.json();
}
async function sendJson(url, method, data){
    const r = await fetch(url, {
        method,
        credentials:'include',
        headers:{ 'Content-Type':'application/json' },
        body: data != null ? JSON.stringify(data) : undefined
    });
    if(!r.ok) throw new Error(await r.text().catch(()=>r.status));
    return r.headers.get('content-type')?.includes('application/json') ? r.json() : {};
}
function mountTemplate(id){
    const tpl  = document.getElementById(id);
    const view = document.getElementById('view');
    if(!tpl?.content || !view) return;
    view.innerHTML = '';
    view.appendChild(tpl.content.cloneNode(true));
}
function fillSelect(sel, items, getVal, getLabel, withEmpty=false){
    const opts = (items||[]).map(it=>{
        const val   = String(getVal(it));
        const label = getLabel(it);
        return `<option value="${val}">${escapeHtml(String(label ?? ''))}</option>`;
    }).join('');
    sel.innerHTML = withEmpty
        ? `<option value="" selected disabled>— wybierz —</option>${opts}`
        : opts;
}
function safeVal(v){ return v == null ? '' : String(v); }

// Modal potwierdzenia z fallbackiem
function confirmModal(message, title='Potwierdzenie'){
    // fallback bez Bootstrapa
    if(!window.bootstrap){
        return Promise.resolve(window.confirm(message));
    }
    return new Promise(resolve=>{
        const modalEl = document.getElementById('confirmModal');
        const modal   = new bootstrap.Modal(modalEl);
        $('#confirmTitle').textContent = title;
        $('#confirmBody').textContent  = message;

        const yesBtn = $('#confirmYesBtn');
        const onYes  = ()=>{ resolve(true); modal.hide(); };
        const onHide = ()=>{ resolve(false); cleanup(); };

        function cleanup(){
            yesBtn.removeEventListener('click', onYes);
            modalEl.removeEventListener('hidden.bs.modal', onHide);
        }

        yesBtn.addEventListener('click', onYes, { once:true });
        modalEl.addEventListener('hidden.bs.modal', onHide, { once:true });
        modal.show();
    });
}

/* ===== Router ===== */
const routes = {
    '#remarks'   : renderRemarks,
    '#grades'    : renderGrades,
    '#schedule'  : renderSchedule,
    '#attendance': renderAttendance,
    '#classes'   : renderClasses,
    '#users'     : renderUsers
};
function handleRoute(){
    const hash = location.hash || '#remarks';
    const fn = routes[hash] || routes['#remarks'];
    return fn();
}

/* ===== Uwagi ===== */
async function renderRemarks(){
    mountTemplate('tpl-remarks');
    const tbody  = $('#remarksTbody');
    const filter = $('#remarksFilter');
    const addBtn = $('#remarkAddBtn');

    let data = [];
    let dictTeachers = null, dictStudents = null;

    async function ensureDicts(){
        if(!dictTeachers) dictTeachers = await getJson('/api/admin/teachers');
        if(!dictStudents) dictStudents = await getJson('/api/admin/students');
    }
    async function load(){
        data = await getJson('/api/admin/remarks');
        draw();
    }
    function draw(){
        const q = (filter.value||'').toLowerCase();
        const rows = (data||[])
            .filter(r => `${r.studentFirstName} ${r.studentLastName} ${r.teacherFirstName} ${r.teacherLastName} ${r.content}`
                .toLowerCase().includes(q))
            .map(r=>`
        <tr data-id="${safeVal(r.id)}">
          <td>${escapeHtml(`${r.studentFirstName??''} ${r.studentLastName??''}`.trim())}</td>
          <td>${escapeHtml(`${r.teacherFirstName??''} ${r.teacherLastName??''}`.trim())}</td>
          <td class="text-truncate" style="max-width:420px">${escapeHtml(r.content??'')}</td>
          <td>${escapeHtml(r.addDate??'')}</td>
          <td class="text-nowrap">
            <button class="btn btn-sm btn-outline-secondary me-1" data-action="edit"><i class="bi bi-pencil"></i></button>
            <button class="btn btn-sm btn-outline-danger" data-action="del"><i class="bi bi-trash"></i></button>
          </td>
        </tr>`).join('');
        tbody.innerHTML = rows || `<tr><td colspan="5" class="text-center text-secondary">Brak danych</td></tr>`;
    }
    async function delRow(id){
        const ok = await confirmModal('Usunąć tę uwagę?', 'Usuń uwagę');
        if(!ok) return;
        try{
            await sendJson(`/api/admin/remarks/${id}`,'DELETE');
            toast('Usunięto');
            await load();
        }catch{
            toast('Błąd usuwania','error');
        }
    }
    async function openModal(mode, id=null){
        await ensureDicts();
        const modalEl   = document.getElementById('modalRemark');
        const modal     = new bootstrap.Modal(modalEl);
        const title     = document.getElementById('remarkModalTitle');
        const idEl      = document.getElementById('remarkId');
        const teacherEl = document.getElementById('remarkTeacher');
        const studentEl = document.getElementById('remarkStudent');
        const contentEl = document.getElementById('remarkContent');
        const saveBtn   = document.getElementById('remarkSaveBtn');

        fillSelect(teacherEl, dictTeachers, t=>t.userId, t=>`${t.firstname} ${t.lastname}`, true);
        fillSelect(studentEl, dictStudents, s=>s.userId, s=>`${s.firstname} ${s.lastname}`, true);

        if(mode==='add'){
            title.textContent='Dodaj uwagę';
            idEl.value=''; teacherEl.value=''; studentEl.value=''; contentEl.value='';
        }else{
            title.textContent='Edytuj uwagę';
            const r = (data||[]).find(x=>String(x.id)===String(id));
            idEl.value   = r?.id ?? '';
            teacherEl.value = r?.teacherId ?? '';
            studentEl.value = r?.studentId ?? '';
            contentEl.value = r?.content ?? '';
        }

        saveBtn.onclick = async ()=>{
            const payload = {
                teacherId: Number(teacherEl.value),
                studentId: Number(studentEl.value),
                content: (contentEl.value||'').trim()
            };
            if(!payload.teacherId || !payload.studentId || !payload.content){
                toast('Uzupełnij wszystkie pola','error'); return;
            }
            try{
                const rowId = idEl.value;
                if(rowId){
                    await sendJson(`/api/admin/remarks/${rowId}`,'PUT',payload);
                    toast('Zapisano');
                }else{
                    await sendJson('/api/admin/remarks','POST',payload);
                    toast('Dodano');
                }
                modal.hide(); await load();
            }catch{
                toast('Błąd zapisu','error');
            }
        };

        modal.show();
    }

    // delegacja klików w tabeli
    tbody.addEventListener('click', (e)=>{
        const btn = e.target.closest('button[data-action]');
        if(!btn) return;
        const id = btn.closest('tr')?.dataset.id;
        if(btn.dataset.action==='edit') openModal('edit', id);
        if(btn.dataset.action==='del')  delRow(id);
    });

    addBtn.addEventListener('click', ()=>openModal('add'));
    filter.addEventListener('input', draw);
    await load();
}

/* ===== Oceny ===== */
async function renderGrades(){
    mountTemplate('tpl-grades');
    const tbody = $('#gradesTbody');
    const addBtn = $('#gradeAddBtn');

    let data = [];
    async function load(){ data = await getJson('/api/admin/grades'); draw(); }
    function draw(){
        const rows = (data||[]).map(g=>{
            const val = g?.value != null ? Number(g.value) : null;
            const valStr = val != null ? val.toFixed(val % 1 === 0 ? 0 : 1) : '';
            return `
        <tr data-id="${safeVal(g.gradeId)}">
          <td>${escapeHtml(`${g.studentFirstName??''} ${g.studentLastName??''}`.trim())}</td>
          <td>${escapeHtml(g.subject ?? g.subjectCode ?? '')}</td>
          <td>${escapeHtml(valStr)}</td>
          <td>${escapeHtml(g.gradeDate ?? '')}</td>
          <td>${escapeHtml(`${g.teacherFirstName??''} ${g.teacherLastName??''}`.trim())}</td>
          <td class="text-nowrap">
            <button class="btn btn-sm btn-outline-secondary me-1" data-action="edit"><i class="bi bi-pencil"></i></button>
            <button class="btn btn-sm btn-outline-danger" data-action="del"><i class="bi bi-trash"></i></button>
          </td>
        </tr>`;
        }).join('');
        tbody.innerHTML = rows || `<tr><td colspan="6" class="text-center text-secondary">Brak danych</td></tr>`;
    }
    async function delRow(id){
        const ok = await confirmModal('Usunąć tę ocenę?', 'Usuń ocenę');
        if(!ok) return;
        try{
            await sendJson(`/api/admin/grades/${id}`,'DELETE');
            toast('Usunięto ocenę');
            await load();
        }catch{
            toast('Błąd usuwania','error');
        }
    }
    async function openModal(mode, id=null){
        const modalEl   = document.getElementById('modalGrade');
        const modal     = new bootstrap.Modal(modalEl);
        const title     = document.getElementById('gradeModalTitle');
        const idEl      = document.getElementById('gradeId');
        const subjectEl = document.getElementById('gradeSubject');
        const valueEl   = document.getElementById('gradeValue');
        const teacherEl = document.getElementById('gradeTeacher');
        const studentEl = document.getElementById('gradeStudent');
        const saveBtn   = document.getElementById('gradeSaveBtn');

        // słowniki
        const [subjects, teachers, students] = await Promise.all([
            getJson('/api/admin/subjects'),
            getJson('/api/admin/teachers'),
            getJson('/api/admin/students'),
        ]);
        fillSelect(subjectEl, subjects, s=>s.name ?? s.code ?? s, s=>s.displayName ?? s.name ?? s, true);
        fillSelect(teacherEl, teachers, t=>t.userId, t=>`${t.firstname} ${t.lastname}`, true);
        fillSelect(studentEl, students, s=>s.userId, s=>`${s.firstname} ${s.lastname}`, true);

        if(mode==='add'){
            title.textContent='Dodaj ocenę';
            idEl.value=''; subjectEl.value=''; valueEl.value=''; teacherEl.value=''; studentEl.value='';
        }else{
            title.textContent='Edytuj ocenę';
            const g = (data||[]).find(x=>String(x.gradeId)===String(id));
            idEl.value = g?.gradeId ?? '';
            subjectEl.value = g?.subjectCode ?? g?.subject ?? '';
            valueEl.value   = g?.value ?? '';
            teacherEl.value = g?.teacherId ?? '';
            studentEl.value = g?.studentId ?? '';
        }

        saveBtn.onclick = async ()=>{
            const payload = {
                subject : subjectEl.value || null,
                value   : valueEl.value ? Number(valueEl.value) : null,
                teacherId: teacherEl.value ? Number(teacherEl.value) : null,
                studentId: studentEl.value ? Number(studentEl.value) : null
            };
            if(!payload.subject || payload.value==null || !payload.teacherId || !payload.studentId){
                toast('Uzupełnij wszystkie pola','error'); return;
            }
            if(payload.value < 1 || payload.value > 6){
                toast('Ocena musi być w zakresie 1–6','error'); return;
            }
            try{
                const rowId = idEl.value;
                if(rowId){
                    await sendJson(`/api/admin/grades/${rowId}`,'PUT',payload);
                    toast('Zapisano');
                }else{
                    await sendJson('/api/admin/grades','POST',payload);
                    toast('Dodano ocenę');
                }
                modal.hide(); await load();
            }catch{
                toast('Błąd zapisu','error');
            }
        };

        modal.show();
    }

    tbody.addEventListener('click', (e)=>{
        const btn = e.target.closest('button[data-action]');
        if(!btn) return;
        const id = btn.closest('tr')?.dataset.id;
        if(btn.dataset.action==='edit') openModal('edit', id);
        if(btn.dataset.action==='del')  delRow(id);
    });
    addBtn.addEventListener('click', ()=>openModal('add'));

    await load();
}

/* ===== Plan ===== */
async function renderSchedule(){
    mountTemplate('tpl-schedule');
    const tbody = $('#scheduleTbody');
    const addBtn = $('#scheduleAddBtn');

    let data = [];
    let dictSubjects=null, dictTeachers=null, dictClasses=null;

    async function ensureDicts(){
        if(!dictSubjects) dictSubjects = await getJson('/api/admin/subjects');
        if(!dictTeachers) dictTeachers = await getJson('/api/admin/teachers');
        if(!dictClasses)  dictClasses  = await getJson('/api/admin/classes');
    }
    async function load(){
        data = await getJson('/api/admin/schedule');
        draw();
    }
    function draw(){
        const rows = (data||[]).map(s=>`
      <tr data-id="${safeVal(s.id)}">
        <td>${escapeHtml(s.date??'')}</td>
        <td>${escapeHtml(s.subjectName??'')}</td>
        <td>${escapeHtml(s.className??'')}</td>
        <td>${escapeHtml(`${s.teacherFirstName??''} ${s.teacherLastName??''}`.trim())}</td>
        <td class="text-nowrap">
          <button class="btn btn-sm btn-outline-secondary me-1" data-action="edit"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-outline-danger" data-action="del"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`).join('');
        tbody.innerHTML = rows || `<tr><td colspan="5" class="text-center text-secondary">Brak danych</td></tr>`;
    }
    async function delRow(id){
        const ok = await confirmModal('Usunąć pozycję planu?', 'Usuń z planu');
        if(!ok) return;
        try{
            await sendJson(`/api/admin/schedule/${id}`,'DELETE');
            toast('Usunięto'); await load();
        }catch{
            toast('Błąd usuwania','error');
        }
    }
    async function openModal(mode, id=null){
        await ensureDicts();

        const modalEl  = document.getElementById('modalSchedule');
        const modal    = new bootstrap.Modal(modalEl);
        const title    = document.getElementById('scheduleModalTitle');
        const idEl     = document.getElementById('scheduleId');
        const dateEl   = document.getElementById('scheduleDate');
        const subjEl   = document.getElementById('scheduleSubject');
        const classEl  = document.getElementById('scheduleClass');
        const teacherEl= document.getElementById('scheduleTeacher');
        const saveBtn  = document.getElementById('scheduleSaveBtn');

        fillSelect(subjEl, dictSubjects, s=>s.name, s=>s.displayName ?? s.name, true);
        fillSelect(teacherEl, dictTeachers, t=>t.userId, t=>`${t.firstname} ${t.lastname}`, true);
        fillSelect(classEl, dictClasses, c=>(c.schoolClassId??c.id??c.classId), c=>(c.name ?? `Klasa ${c.schoolClassId??c.id??''}`), true);

        if(mode==='add'){
            title.textContent='Dodaj';
            idEl.value=''; dateEl.value=''; subjEl.value=''; classEl.value=''; teacherEl.value='';
        }else{
            title.textContent='Edytuj';
            const s = (data||[]).find(x=>String(x.id)===String(id));
            idEl.value   = s?.id ?? '';
            dateEl.value = s?.date ?? '';
            teacherEl.value = s?.teacherId ?? '';
            // jeśli backend zwraca subjectCode / classId – ustaw, jeśli nie, zostaw selekt do wyboru
            subjEl.value  = s?.subjectCode ?? '';
            classEl.value = s?.classId ?? '';
        }

        saveBtn.onclick = async ()=>{
            const payload = {
                date: dateEl.value,
                subjectName: subjEl.value,      // jeżeli API oczekuje code/enum: podmień na subjectCode
                className: String(classEl.value),// jw. jeśli oczekuje id: classId
                teacherId: Number(teacherEl.value)
            };
            if(!payload.date || !payload.subjectName || !payload.className || !payload.teacherId){
                toast('Uzupełnij wszystkie pola','error'); return;
            }
            try{
                const rowId = idEl.value;
                if(rowId){
                    await sendJson(`/api/admin/schedule/${rowId}`,'PUT',payload);
                    toast('Zapisano');
                }else{
                    await sendJson('/api/admin/schedule','POST',payload);
                    toast('Dodano');
                }
                modal.hide(); await load();
            }catch{
                toast('Błąd zapisu','error');
            }
        };

        modal.show();
    }

    tbody.addEventListener('click', (e)=>{
        const btn = e.target.closest('button[data-action]');
        if(!btn) return;
        const id = btn.closest('tr')?.dataset.id;
        if(btn.dataset.action==='edit') openModal('edit', id);
        if(btn.dataset.action==='del')  delRow(id);
    });
    addBtn.addEventListener('click', ()=>openModal('add'));

    await load();
}

/* ===== Frekwencja ===== */
async function renderAttendance(){
    mountTemplate('tpl-attendance');
    const tbody   = $('#attendanceTbody');
    const addBtn  = $('#attendanceAddBtn');

    let data = [];
    let dictSubjects=null, dictStudents=null, dictTeachers=null;

    async function ensureDicts(){
        if(!dictSubjects) dictSubjects = await getJson('/api/admin/subjects');
        if(!dictStudents) dictStudents = await getJson('/api/admin/students');
        if(!dictTeachers) dictTeachers = await getJson('/api/admin/teachers');
    }
    async function load(){
        data = await getJson('/api/admin/attendance');
        draw();
    }
    function draw(){
        const rows = (data||[]).map(a=>{
            const id = a.attendanceId ?? a.id;
            return `
        <tr data-id="${safeVal(id)}">
          <td>${escapeHtml(a.attendanceDate??'')}</td>
          <td>${escapeHtml(`${a.studentFirstName??''} ${a.studentLastName??''}`.trim())}</td>
          <td>${escapeHtml(a.subjectName??'')}</td>
          <td>${escapeHtml(a.attendanceStatus??'')}</td>
          <td>${escapeHtml(`${a.teacherFirstName??''} ${a.teacherLastName??''}`.trim())}</td>
          <td class="text-nowrap">
            <button class="btn btn-sm btn-outline-secondary me-1" data-action="edit"><i class="bi bi-pencil"></i></button>
            <button class="btn btn-sm btn-outline-danger" data-action="del"><i class="bi bi-trash"></i></button>
          </td>
        </tr>`;
        }).join('');
        tbody.innerHTML = rows || `<tr><td colspan="6" class="text-center text-secondary">Brak danych</td></tr>`;
    }
    async function delRow(id){
        const ok = await confirmModal('Usunąć wpis frekwencji?', 'Usuń frekwencję');
        if(!ok) return;
        try{
            await sendJson(`/api/admin/attendance/${id}`,'DELETE');
            toast('Usunięto'); await load();
        }catch{
            toast('Błąd usuwania','error');
        }
    }
    async function openModal(mode, id=null){
        await ensureDicts();

        const modalEl = document.getElementById('modalAttendance');
        const modal   = new bootstrap.Modal(modalEl);
        const title   = document.getElementById('attendanceModalTitle');
        const idEl    = document.getElementById('attendanceId');
        const dateEl  = document.getElementById('attendanceDate');
        const subjEl  = document.getElementById('attendanceSubject');
        const studEl  = document.getElementById('attendanceStudent');
        const teachEl = document.getElementById('attendanceTeacher');
        const statusEl= document.getElementById('attendanceStatus');
        const saveBtn = document.getElementById('attendanceSaveBtn');

        fillSelect(subjEl,  dictSubjects, s=>s.name,   s=>s.displayName ?? s.name, true);
        fillSelect(studEl,  dictStudents, s=>s.userId, s=>`${s.firstname} ${s.lastname}`, true);
        fillSelect(teachEl, dictTeachers, t=>t.userId, t=>`${t.firstname} ${t.lastname}`, true);

        if(mode==='add'){
            title.textContent='Dodaj wpis';
            idEl.value=''; dateEl.value=''; subjEl.value=''; studEl.value=''; teachEl.value=''; statusEl.value='';
        }else{
            title.textContent='Edytuj wpis';
            const a = await getJson(`/api/admin/attendance/${id}`);
            idEl.value   = a?.attendanceId ?? '';
            dateEl.value = a?.attendanceDate ?? '';

            // subject
            subjEl.value = a?.subjectEnum ?? '';
            if(!subjEl.value && a?.subjectName){
                const opt = [...subjEl.options].find(o=>o.textContent === a.subjectName);
                if(opt) subjEl.value = opt.value;
            }
            // student & teacher
            studEl.value = a?.studentId ? String(a.studentId) : '';
            teachEl.value= a?.teacherId ? String(a.teacherId) : '';

            // status (obsługa ewentualnych display names)
            const displayToCode = { 'Obecny':'PRESENT','Nieobecny':'ABSENT','Spóźnienie':'LATE','Usprawiedliwione':'EXCUSED' };
            const statusCodes   = ['PRESENT','ABSENT','LATE','EXCUSED'];
            let code = a?.attendanceStatus ?? '';
            if(!statusCodes.includes(code)) code = displayToCode[code] ?? '';
            statusEl.value = code;
        }

        saveBtn.onclick = async ()=>{
            const payload = {
                attendanceDate : dateEl.value,
                subjectEnum    : subjEl.value,
                studentId      : Number(studEl.value),
                teacherId      : Number(teachEl.value),
                attendanceStatus: statusEl.value
            };
            if(!payload.attendanceDate || !payload.subjectEnum || !payload.studentId || !payload.teacherId || !payload.attendanceStatus){
                toast('Uzupełnij wszystkie pola','error'); return;
            }
            try{
                const rowId = idEl.value;
                if(rowId){
                    await sendJson(`/api/admin/attendance/${rowId}`,'PUT',payload);
                    toast('Zapisano');
                }else{
                    await sendJson('/api/admin/attendance','POST',payload);
                    toast('Dodano');
                }
                modal.hide(); await load();
            }catch{
                toast('Błąd zapisu','error');
            }
        };

        modal.show();
    }

    tbody.addEventListener('click', (e)=>{
        const btn = e.target.closest('button[data-action]');
        if(!btn) return;
        const id = btn.closest('tr')?.dataset.id;
        if(btn.dataset.action==='edit') openModal('edit', id);
        if(btn.dataset.action==='del')  delRow(id);
    });
    addBtn.addEventListener('click', ()=>openModal('add'));

    await load();
}

/* ===== Klasy ===== */
async function renderClasses(){
    mountTemplate('tpl-classes');
    const tbody = $('#classesTbody');
    const addBtn = $('#classAddBtn');

    let data = [];
    async function load(){
        data = await getJson('/api/admin/classes');
        draw();
    }
    function draw(){
        const rows = (data||[]).map(c=>`
      <tr data-id="${safeVal(c.schoolClassId??c.id??c.classId)}">
        <td>${escapeHtml(c.name??'')}</td>
        <td>${escapeHtml(String(c.studentCount != null ? c.studentCount : (c.students?.length ?? 0)))}</td>
        <td class="text-nowrap">
          <button class="btn btn-sm btn-outline-danger" data-action="del"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`).join('');
        tbody.innerHTML = rows || `<tr><td colspan="3" class="text-center text-secondary">Brak danych</td></tr>`;
    }
    async function delRow(id){
        const ok = await confirmModal('Usunąć klasę? Operacja nie powiedzie się, jeśli są powiązane rekordy planu.', 'Usuń klasę');
        if(!ok) return;
        try{
            await sendJson(`/api/admin/classes/${id}`,'DELETE');
            toast('Usunięto'); await load();
        }catch{
            toast('Nie można usunąć klasy – usuń najpierw powiązany plan lub przepnij rekordy.','error');
        }
    }
    function openModal(mode,id=null){
        const modalEl = document.getElementById('modalClass');
        const modal   = new bootstrap.Modal(modalEl);
        const title   = document.getElementById('classModalTitle');
        const idEl    = document.getElementById('classId');
        const nameEl  = document.getElementById('className');
        const saveBtn = document.getElementById('classSaveBtn');

        if(mode==='add'){
            title.textContent='Dodaj klasę';
            idEl.value=''; nameEl.value='';
        }else{
            title.textContent='Edytuj klasę';
            const c = (data||[]).find(x=>String(x.schoolClassId??x.id??x.classId)===String(id));
            idEl.value = c?.schoolClassId ?? c?.id ?? c?.classId ?? '';
            nameEl.value = c?.name ?? '';
        }

        saveBtn.onclick = async ()=>{
            const payload = { name:(nameEl.value||'').trim() };
            if(!payload.name){ toast('Nazwa wymagana','error'); return; }
            try{
                if(idEl.value){
                    toast('Edycja klasy nieobsługiwana w API – zmień nazwę przez usunięcie/dodanie.','error');
                }else{
                    await sendJson('/api/admin/classes','POST',payload);
                    toast('Dodano'); modal.hide(); await load();
                }
            }catch{
                toast('Błąd zapisu','error');
            }
        };

        modal.show();
    }

    tbody.addEventListener('click', (e)=>{
        const btn = e.target.closest('button[data-action]');
        if(!btn) return;
        const id = btn.closest('tr')?.dataset.id;
        if(btn.dataset.action==='del') delRow(id);
    });
    addBtn.addEventListener('click', ()=>openModal('add'));

    await load();
}

/* ===== Użytkownicy ===== */
async function renderUsers(){
    mountTemplate('tpl-users');
    const tbody = $('#usersTbody');
    const addBtn = $('#userAddBtn');

    let data = [];
    async function load(){
        data = await getJson('/api/admin/users');
        draw();
    }
    function draw(){
        const rows = (data||[]).map(u=>`
      <tr data-id="${safeVal(u.id)}">
        <td>${escapeHtml(u.firstname??'')}</td>
        <td>${escapeHtml(u.lastname??'')}</td>
        <td>${escapeHtml(u.username??'')}</td>
        <td>${escapeHtml(u.email??'')}</td>
        <td>${escapeHtml(u.role??'')}</td>
        <td class="text-nowrap">
          <button class="btn btn-sm btn-outline-secondary me-1" data-action="edit"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-outline-danger" data-action="del"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`).join('');
        tbody.innerHTML = rows || `<tr><td colspan="6" class="text-center text-secondary">Brak danych</td></tr>`;
    }
    async function delRow(id){
        const ok = await confirmModal('Usunąć użytkownika?', 'Usuń użytkownika');
        if(!ok) return;
        try{
            await sendJson(`/api/admin/users/${id}`,'DELETE');
            toast('Usunięto'); await load();
        }catch{
            toast('Błąd usuwania','error');
        }
    }
    function openModal(mode,id=null){
        const modalEl = document.getElementById('modalUser');
        const modal   = new bootstrap.Modal(modalEl);
        const title   = document.getElementById('userModalTitle');
        const idEl    = document.getElementById('userId');
        const fEl     = document.getElementById('userFirst');
        const lEl     = document.getElementById('userLast');
        const uEl     = document.getElementById('userLogin');
        const eEl     = document.getElementById('userEmail');
        const rEl     = document.getElementById('userRole');
        const pEl     = document.getElementById('userPassword');
        const p2El    = document.getElementById('userPassword2');
        const saveBtn = document.getElementById('userSaveBtn');

        if(mode==='add'){
            title.textContent='Dodaj użytkownika';
            idEl.value=''; fEl.value=''; lEl.value=''; uEl.value=''; eEl.value=''; rEl.value=''; pEl.value=''; p2El.value='';
        }else{
            title.textContent='Edytuj użytkownika';
            const u = (data||[]).find(x=>String(x.id)===String(id));
            idEl.value = u?.id ?? '';
            fEl.value  = u?.firstname ?? '';
            lEl.value  = u?.lastname ?? '';
            uEl.value  = u?.username ?? '';
            eEl.value  = u?.email ?? '';
            const roleMap = { 'Administrator':'ADMIN','Nauczyciel':'TEACHER','Uczeń':'STUDENT','Rodzic':'PARENT' };
            const code = (u?.role||'').toUpperCase();
            rEl.value = ['ADMIN','TEACHER','STUDENT','PARENT'].includes(code) ? code : (roleMap[u?.role] ?? '');
            pEl.value=''; p2El.value='';
        }

        saveBtn.onclick = async ()=>{
            if(pEl.value || p2El.value){
                if(pEl.value !== p2El.value){ toast('Hasła nie są zgodne','error'); return; }
                if(pEl.value.length < 6){ toast('Hasło musi mieć co najmniej 6 znaków','error'); return; }
            }
            const payload = {
                firstname: fEl.value?.trim(),
                lastname : lEl.value?.trim(),
                username : uEl.value?.trim(),
                email    : eEl.value?.trim(),
                role     : rEl.value
            };
            if(pEl.value) payload.password = pEl.value;

            if(!payload.firstname || !payload.lastname || !payload.username || !payload.email || !payload.role){
                toast('Uzupełnij wymagane pola','error'); return;
            }
            try{
                const rowId = idEl.value;
                if(rowId){
                    await sendJson(`/api/admin/users/${rowId}`,'PUT',payload);
                    toast('Zapisano');
                }else{
                    await sendJson('/api/admin/users','POST',payload);
                    toast('Dodano użytkownika');
                }
                modal.hide(); await load();
            }catch{
                toast('Błąd zapisu','error');
            }
        };

        modal.show();
    }

    tbody.addEventListener('click', (e)=>{
        const btn = e.target.closest('button[data-action]');
        if(!btn) return;
        const id = btn.closest('tr')?.dataset.id;
        if(btn.dataset.action==='edit') openModal('edit', id);
        if(btn.dataset.action==='del')  delRow(id);
    });
    addBtn.addEventListener('click', ()=>openModal('add'));

    await load();
}

/* ===== start ===== */
async function init(){
    window.addEventListener('hashchange', handleRoute);
    await handleRoute();
}

if(document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', () => { init().catch(console.error); });
}else{
    init().catch(console.error);
}
