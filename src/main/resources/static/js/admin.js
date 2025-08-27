import { toast } from '/js/common.js';

/* tiny helpers */
const $ = sel => document.querySelector(sel);
const $$ = sel => Array.from(document.querySelectorAll(sel));
async function getJson(url){
    const r = await fetch(url, { credentials: 'include' });
    if(!r.ok) throw new Error(await r.text().catch(()=>r.status));
    return r.json();
}
async function sendJson(url, method, data){
    const r = await fetch(url, {
        method, credentials:'include',
        headers:{'Content-Type':'application/json'},
        body: data ? JSON.stringify(data) : undefined
    });
    if(!r.ok) throw new Error(await r.text().catch(()=>r.status));
    return r.headers.get('content-type')?.includes('application/json') ? r.json() : {};
}
function mountTemplate(tplId){
    const tpl = document.getElementById(tplId);
    const view = document.getElementById('view');
    view.innerHTML = '';
    view.appendChild(tpl.content.cloneNode(true));
}
function fillSelect(selectEl, items, getVal, getLabel){
    selectEl.innerHTML = items.map(it => `<option value="${getVal(it)}">${getLabel(it)}</option>`).join('');
}

/* -------- Router -------- */
const routes = {
    '#remarks': renderRemarks,
    '#grades' : renderGrades,
    '#schedule': renderSoon,
    '#attendance': renderSoon,
    '#classes': renderSoon,
    '#users': renderSoon
};

function renderSoon(){
    const view = document.getElementById('view');
    view.innerHTML = `<div class="card-glass p-4 text-center text-secondary">Ten moduł przeniesiemy w kolejnym kroku 🙂</div>`;
}

async function handleRoute(){
    const hash = location.hash || '#remarks';
    const fn = routes[hash] || routes['#remarks'];
    await fn();
}

/* ======== REMARKS ======== */
async function renderRemarks(){
    mountTemplate('tpl-remarks');

    const tbody = document.getElementById('remarksTbody');
    const filter = document.getElementById('remarksFilter');

    let data = [];
    async function load(){
        data = await getJson('/api/admin/remarks');
        draw();
    }
    function draw(){
        const q = (filter.value || '').toLowerCase();
        const rows = data
            .filter(r => `${r.studentFirstName} ${r.studentLastName} ${r.teacherFirstName} ${r.teacherLastName}`.toLowerCase().includes(q))
            .map(r => `
        <tr data-id="${r.id}">
          <td>${r.studentFirstName} ${r.studentLastName}</td>
          <td>${r.teacherFirstName} ${r.teacherLastName}</td>
          <td class="text-truncate" style="max-width:420px">${r.content ?? ''}</td>
          <td>${r.addDate ?? ''}</td>
          <td class="text-nowrap">
            <button class="btn btn-sm btn-outline-secondary me-1 btn-edit"><i class="bi bi-pencil"></i></button>
            <button class="btn btn-sm btn-outline-danger btn-del"><i class="bi bi-trash"></i></button>
          </td>
        </tr>
      `).join('');
        tbody.innerHTML = rows || `<tr><td colspan="5" class="text-center text-secondary">Brak danych</td></tr>`;

        tbody.querySelectorAll('.btn-edit').forEach(btn=>{
            btn.addEventListener('click', () => openRemarkModal(btn.closest('tr').dataset.id));
        });
        tbody.querySelectorAll('.btn-del').forEach(btn=>{
            btn.addEventListener('click', () => delRemark(btn.closest('tr').dataset.id));
        });
    }

    async function delRemark(id){
        if(!confirm('Usunąć tę uwagę?')) return;
        try{
            await sendJson(`/api/admin/remarks/${id}`, 'DELETE');
            toast('Usunięto uwagę');
            await load();
        }catch(e){ toast('Błąd usuwania', 'error'); }
    }

    const modalEl = document.getElementById('modalRemark');
    const modal = new bootstrap.Modal(modalEl);
    const contentEl = document.getElementById('remarkContent');
    const saveBtn = document.getElementById('remarkSaveBtn');
    let currentRemarkId = null;

    async function openRemarkModal(id){
        currentRemarkId = id;
        const row = data.find(x => String(x.id) === String(id));
        contentEl.value = row?.content ?? '';
        modal.show();
    }
    saveBtn.addEventListener('click', async ()=>{
        try{
            await sendJson(`/api/admin/remarks/${currentRemarkId}`, 'PUT', { content: contentEl.value });
            modal.hide();
            toast('Zapisano');
            await load();
        }catch(e){ toast('Błąd zapisu', 'error'); }
    });

    filter.addEventListener('input', draw);
    await load();
}

/* ======== GRADES ======== */
async function renderGrades(){
    mountTemplate('tpl-grades');

    const tbody = document.getElementById('gradesTbody');
    const addBtn = document.getElementById('gradeAddBtn');

    let data = [];
    async function load(){
        data = await getJson('/api/admin/grades');
        draw();
    }
    function draw(){
        const rows = data.map(g => `
      <tr data-id="${g.gradeId}">
        <td>${g.studentFirstName} ${g.studentLastName}</td>
        <td>${g.subject}</td>
        <td>${g.value}</td>
        <td>${g.gradeDate ?? ''}</td>
        <td>${g.teacherFirstName} ${g.teacherLastName}</td>
        <td class="text-nowrap">
          <button class="btn btn-sm btn-outline-secondary me-1 btn-edit"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-outline-danger btn-del"><i class="bi bi-trash"></i></button>
        </td>
      </tr>
    `).join('');
        tbody.innerHTML = rows || `<tr><td colspan="6" class="text-center text-secondary">Brak danych</td></tr>`;

        tbody.querySelectorAll('.btn-edit').forEach(btn=>{
            btn.addEventListener('click', () => openGradeModal('edit', btn.closest('tr').dataset.id));
        });
        tbody.querySelectorAll('.btn-del').forEach(btn=>{
            btn.addEventListener('click', () => delGrade(btn.closest('tr').dataset.id));
        });
    }

    async function delGrade(id){
        if(!confirm('Usunąć tę ocenę?')) return;
        try{
            await sendJson(`/api/admin/grades/${id}`, 'DELETE');
            toast('Usunięto ocenę');
            await load();
        }catch(e){ toast('Błąd usuwania', 'error'); }
    }

    const modalEl = document.getElementById('modalGrade');
    const modal = new bootstrap.Modal(modalEl);
    const title = document.getElementById('gradeModalTitle');
    const idEl = document.getElementById('gradeId');
    const subjectEl = document.getElementById('gradeSubject');
    const valueEl = document.getElementById('gradeValue');
    const teacherEl = document.getElementById('gradeTeacher');
    const studentEl = document.getElementById('gradeStudent');
    const saveBtn = document.getElementById('gradeSaveBtn');

    let dictSubjects = null, dictTeachers = null, dictStudents = null;

    async function ensureDicts(){
        if(!dictSubjects) dictSubjects = await getJson('/api/admin/subjects');
        if(!dictTeachers) dictTeachers = await getJson('/api/admin/teachers');
        if(!dictStudents) dictStudents = await getJson('/api/admin/students');

        fillSelect(subjectEl, dictSubjects, s => s.name, s => s.displayName ?? s.name);
        fillSelect(teacherEl, dictTeachers, t => t.userId, t => `${t.firstname} ${t.lastname}`);
        fillSelect(studentEl, dictStudents, s => s.userId, s => `${s.firstname} ${s.lastname}`);
    }

    async function openGradeModal(mode, id = null){
        await ensureDicts();
        if(mode === 'add'){
            title.textContent = 'Dodaj ocenę';
            idEl.value = '';
            subjectEl.selectedIndex = 0;
            valueEl.value = '';
            teacherEl.selectedIndex = 0;
            studentEl.selectedIndex = 0;
        }else{
            title.textContent = 'Edytuj ocenę';
            const g = data.find(x => String(x.gradeId) === String(id));
            idEl.value = g?.gradeId ?? '';
            subjectEl.value = g?.subject ?? '';
            valueEl.value = g?.value ?? '';
            // dopasowanie nauczyciela/ucznia po imieniu+nazwisku nie jest jednoznaczne — serwer zwróci ID w pełnym API
            // tu zostawimy jako „brak zmian” jeśli nie znamy ID
        }
        modal.show();
    }

    addBtn.addEventListener('click', () => openGradeModal('add'));

    saveBtn.addEventListener('click', async ()=>{
        const payload = {
            subject: subjectEl.value,
            value: Number(valueEl.value),
            teacherId: Number(teacherEl.value),
            studentId: Number(studentEl.value)
        };
        if(!payload.subject || !payload.value || !payload.teacherId || !payload.studentId){
            toast('Uzupełnij wszystkie pola', 'error'); return;
        }
        try{
            const id = idEl.value;
            if(id){
                await sendJson(`/api/admin/grades/${id}`, 'PUT', payload);
                toast('Zapisano');
            }else{
                await sendJson('/api/admin/grades', 'POST', payload);
                toast('Dodano ocenę');
            }
            modal.hide();
            await load();
        }catch(e){ toast('Błąd zapisu', 'error'); }
    });

    await load();
}

/* init */
window.addEventListener('hashchange', handleRoute);
handleRoute();
