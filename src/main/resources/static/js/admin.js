import { toast } from '/js/common.js';

const $ = sel => document.querySelector(sel);
async function getJson(url){ const r = await fetch(url,{credentials:'include'}); if(!r.ok) throw new Error(await r.text().catch(()=>r.status)); return r.json(); }
async function sendJson(url, method, data){ const r = await fetch(url,{method,credentials:'include',headers:{'Content-Type':'application/json'},body:data?JSON.stringify(data):undefined}); if(!r.ok) throw new Error(await r.text().catch(()=>r.status)); return r.headers.get('content-type')?.includes('application/json')?r.json():{}; }
function mountTemplate(id){ const tpl=document.getElementById(id); const view=document.getElementById('view'); view.innerHTML=''; view.appendChild(tpl.content.cloneNode(true)); }
function fillSelect(sel, items, getVal, getLabel, withEmpty=false){ const opts=items.map(it=>`<option value="${getVal(it)}">${getLabel(it)}</option>`).join(''); sel.innerHTML = withEmpty?`<option value="" selected disabled>— wybierz —</option>${opts}`:opts; }

// Modal potwierdzenia
function confirmModal(message, title='Potwierdzenie'){
    return new Promise(resolve=>{
        const modalEl = document.getElementById('confirmModal');
        const modal = bootstrap ? new bootstrap.Modal(modalEl) : null;
        $('#confirmTitle').textContent = title;
        $('#confirmBody').textContent = message;
        const yesBtn = $('#confirmYesBtn');
        const onHide = () => {
            yesBtn.removeEventListener('click', onYes);
            modalEl.removeEventListener('hidden.bs.modal', onHide);
        };
        const onYes = ()=>{ resolve(true); modal.hide(); };
        yesBtn.addEventListener('click', onYes, { once:true });
        modalEl.addEventListener('hidden.bs.modal', ()=>{ resolve(false); onHide(); }, { once:true });
        modal.show();
    });
}

/* Router */
const routes = {
    '#remarks': renderRemarks,
    '#grades': renderGrades,
    '#schedule': renderSchedule,
    '#attendance': renderAttendance,
    '#classes': renderClasses,
    '#users': renderUsers
};
window.addEventListener('hashchange', ()=>handleRoute());
handleRoute();
async function handleRoute(){ const hash=location.hash||'#remarks'; const fn=routes[hash]||routes['#remarks']; await fn(); }

/* ===== Uwagi ===== */
async function renderRemarks(){
    mountTemplate('tpl-remarks');
    const tbody = document.getElementById('remarksTbody');
    const filter = document.getElementById('remarksFilter');
    const addBtn = document.getElementById('remarkAddBtn');

    let data = [];
    let dictTeachers=null, dictStudents=null;

    async function ensureDicts(){
        if(!dictTeachers) dictTeachers = await getJson('/api/admin/teachers');
        if(!dictStudents) dictStudents = await getJson('/api/admin/students');
    }
    async function load(){ data = await getJson('/api/admin/remarks'); draw(); }
    function draw(){
        const q=(filter.value||'').toLowerCase();
        const rows=data.filter(r=>`${r.studentFirstName} ${r.studentLastName} ${r.teacherFirstName} ${r.teacherLastName}`.toLowerCase().includes(q)).map(r=>`
      <tr data-id="${r.id}">
        <td>${r.studentFirstName} ${r.studentLastName}</td>
        <td>${r.teacherFirstName} ${r.teacherLastName}</td>
        <td class="text-truncate" style="max-width:420px">${r.content??''}</td>
        <td>${r.addDate??''}</td>
        <td class="text-nowrap">
          <button class="btn btn-sm btn-outline-secondary me-1 btn-edit"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-outline-danger btn-del"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`).join('');
        tbody.innerHTML = rows || `<tr><td colspan="5" class="text-center text-secondary">Brak danych</td></tr>`;
        tbody.querySelectorAll('.btn-edit').forEach(b=>b.addEventListener('click',()=>openModal('edit',b.closest('tr').dataset.id)));
        tbody.querySelectorAll('.btn-del').forEach(b=>b.addEventListener('click',()=>delRow(b.closest('tr').dataset.id)));
    }
    async function delRow(id){
        const ok = await confirmModal('Usunąć tę uwagę?', 'Usuń uwagę');
        if(!ok) return;
        try{ await sendJson(`/api/admin/remarks/${id}`,'DELETE'); toast('Usunięto'); await load(); }
        catch{ toast('Błąd usuwania','error'); }
    }

    const modalEl = document.getElementById('modalRemark');
    const modal = new bootstrap.Modal(modalEl);
    const title = document.getElementById('remarkModalTitle');
    const idEl = document.getElementById('remarkId');
    const teacherEl = document.getElementById('remarkTeacher');
    const studentEl = document.getElementById('remarkStudent');
    const contentEl = document.getElementById('remarkContent');
    const saveBtn = document.getElementById('remarkSaveBtn');

    async function openModal(mode, id=null){
        await ensureDicts();
        fillSelect(teacherEl, dictTeachers, t=>t.userId, t=>`${t.firstname} ${t.lastname}`, true);
        fillSelect(studentEl, dictStudents, s=>s.userId, s=>`${s.firstname} ${s.lastname}`, true);
        if(mode==='add'){
            title.textContent='Dodaj uwagę'; idEl.value=''; teacherEl.value=''; studentEl.value=''; contentEl.value='';
        }else{
            title.textContent='Edytuj uwagę';
            const r=data.find(x=>String(x.id)===String(id));
            idEl.value=r?.id??''; teacherEl.value=r?.teacherId??''; studentEl.value=r?.studentId??''; contentEl.value=r?.content??'';
        }
        modal.show();
    }
    addBtn.addEventListener('click',()=>openModal('add'));
    saveBtn.addEventListener('click', async ()=>{
        const payload={ teacherId: Number(teacherEl.value), studentId: Number(studentEl.value), content: (contentEl.value||'').trim() };
        if(!payload.teacherId || !payload.studentId || !payload.content){ toast('Uzupełnij wszystkie pola','error'); return; }
        try{
            const id=idEl.value;
            if(id){ await sendJson(`/api/admin/remarks/${id}`,'PUT',payload); toast('Zapisano'); }
            else { await sendJson('/api/admin/remarks','POST',payload); toast('Dodano'); }
            modal.hide(); await load();
        }catch{ toast('Błąd zapisu','error'); }
    });

    filter.addEventListener('input',draw);
    await load();
}

/* ===== Oceny ===== */
async function renderGrades(){
    mountTemplate('tpl-grades');
    const tbody = document.getElementById('gradesTbody');
    const addBtn = document.getElementById('gradeAddBtn');

    let data = [];
    async function load(){ data = await getJson('/api/admin/grades'); draw(); }
    function draw(){
        const rows = data.map(g => `
      <tr data-id="${g.gradeId}">
        <td>${g.studentFirstName} ${g.studentLastName}</td>
        <td>${g.subject}</td>
        <td>${Number(g.value).toFixed(g.value % 1 === 0 ? 0 : 1)}</td>
        <td>${g.gradeDate ?? ''}</td>
        <td>${g.teacherFirstName} ${g.teacherLastName}</td>
        <td class="text-nowrap">
          <button class="btn btn-sm btn-outline-secondary me-1 btn-edit"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-outline-danger btn-del"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`).join('');
        tbody.innerHTML = rows || `<tr><td colspan="6" class="text-center text-secondary">Brak danych</td></tr>`;
        tbody.querySelectorAll('.btn-edit').forEach(btn=>btn.addEventListener('click',()=>openModal('edit',btn.closest('tr').dataset.id)));
        tbody.querySelectorAll('.btn-del').forEach(btn=>btn.addEventListener('click',()=>delRow(btn.closest('tr').dataset.id)));
    }
    async function delRow(id){
        const ok = await confirmModal('Usunąć tę ocenę?', 'Usuń ocenę');
        if(!ok) return;
        try{ await sendJson(`/api/admin/grades/${id}`,'DELETE'); toast('Usunięto ocenę'); await load(); }
        catch{ toast('Błąd usuwania','error'); }
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

    let dictSubjects=null, dictTeachers=null, dictStudents=null;
    async function ensureDicts(){
        if(!dictSubjects) dictSubjects = await getJson('/api/admin/subjects');
        if(!dictTeachers) dictTeachers = await getJson('/api/admin/teachers');
        if(!dictStudents) dictStudents = await getJson('/api/admin/students');
        fillSelect(subjectEl, dictSubjects, s => s.name, s => s.displayName ?? s.name, true);
        fillSelect(teacherEl, dictTeachers, t => t.userId, t => `${t.firstname} ${t.lastname}`, true);
        fillSelect(studentEl, dictStudents, s => s.userId, s => `${s.firstname} ${s.lastname}`, true);
    }
    async function openModal(mode, id=null){
        await ensureDicts();
        if(mode==='add'){
            title.textContent='Dodaj ocenę'; idEl.value=''; subjectEl.value=''; valueEl.value=''; teacherEl.value=''; studentEl.value='';
        }else{
            title.textContent='Edytuj ocenę';
            const g=data.find(x=>String(x.gradeId)===String(id));
            idEl.value=g?.gradeId??''; subjectEl.value=g?.subjectCode??''; valueEl.value=g?.value??''; teacherEl.value=g?.teacherId??''; studentEl.value=g?.studentId??'';
        }
        modal.show();
    }
    addBtn.addEventListener('click',()=>openModal('add'));
    saveBtn.addEventListener('click', async ()=>{
        const payload={ subject: subjectEl.value, value: valueEl.value?Number(valueEl.value):null, teacherId: teacherEl.value?Number(teacherEl.value):null, studentId: studentEl.value?Number(studentEl.value):null };
        if(!payload.subject || payload.value==null || !payload.teacherId || !payload.studentId){ toast('Uzupełnij wszystkie pola','error'); return; }
        if(payload.value<1 || payload.value>6){ toast('Ocena musi być w zakresie 1–6','error'); return; }
        try{
            const id=idEl.value;
            if(id){ await sendJson(`/api/admin/grades/${id}`,'PUT',payload); toast('Zapisano'); }
            else { await sendJson('/api/admin/grades','POST',payload); toast('Dodano ocenę'); }
            modal.hide(); await load();
        }catch{ toast('Błąd zapisu','error'); }
    });
    await load();
}

/* ===== Plan (ScheduleDto) ===== */
async function renderSchedule(){
    mountTemplate('tpl-schedule');
    const tbody = document.getElementById('scheduleTbody');
    const addBtn = document.getElementById('scheduleAddBtn');
    const modalEl = document.getElementById('modalSchedule');
    const modal = new bootstrap.Modal(modalEl);
    const title = document.getElementById('scheduleModalTitle');
    const idEl = document.getElementById('scheduleId');
    const dateEl = document.getElementById('scheduleDate');
    const subjEl = document.getElementById('scheduleSubject');
    const classEl = document.getElementById('scheduleClass');
    const teacherEl = document.getElementById('scheduleTeacher');
    const saveBtn = document.getElementById('scheduleSaveBtn');

    let data=[], dictSubjects=null, dictTeachers=null, dictClasses=null;
    async function ensureDicts(){
        if(!dictSubjects) dictSubjects=await getJson('/api/admin/subjects');
        if(!dictTeachers) dictTeachers=await getJson('/api/admin/teachers');
        if(!dictClasses) dictClasses=await getJson('/api/admin/classes');
        fillSelect(subjEl, dictSubjects, s=>s.name, s=>s.displayName??s.name, true);
        fillSelect(teacherEl, dictTeachers, t=>t.userId, t=>`${t.firstname} ${t.lastname}`, true);
        fillSelect(classEl, dictClasses, c=> (c.schoolClassId??c.id??c.classId), c=> (c.name??`Klasa ${(c.schoolClassId??c.id??'')}`), true);
    }
    async function load(){ data=await getJson('/api/admin/schedule'); draw(); }
    function draw(){
        const rows=data.map(s=>`
      <tr data-id="${s.id}">
        <td>${s.date??''}</td>
        <td>${s.subjectName??''}</td>
        <td>${s.className??''}</td>
        <td>${(s.teacherFirstName??'')} ${(s.teacherLastName??'')}</td>
        <td class="text-nowrap">
          <button class="btn btn-sm btn-outline-secondary me-1 btn-edit"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-outline-danger btn-del"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`).join('');
        tbody.innerHTML = rows || `<tr><td colspan="5" class="text-center text-secondary">Brak danych</td></tr>`;
        tbody.querySelectorAll('.btn-edit').forEach(b=>b.addEventListener('click',()=>openModal('edit',b.closest('tr').dataset.id)));
        tbody.querySelectorAll('.btn-del').forEach(b=>b.addEventListener('click',()=>delRow(b.closest('tr').dataset.id)));
    }
    async function delRow(id){
        const ok = await confirmModal('Usunąć pozycję planu?', 'Usuń z planu');
        if(!ok) return;
        try{ await sendJson(`/api/admin/schedule/${id}`,'DELETE'); toast('Usunięto'); await load(); }
        catch{ toast('Błąd usuwania','error'); }
    }
    async function openModal(mode, id=null){
        await ensureDicts();
        if(mode==='add'){
            title.textContent='Dodaj'; idEl.value=''; dateEl.value=''; subjEl.value=''; classEl.value=''; teacherEl.value='';
        }else{
            title.textContent='Edytuj';
            const s=data.find(x=>String(x.id)===String(id));
            idEl.value=s?.id??''; dateEl.value=s?.date??''; teacherEl.value=s?.teacherId??'';
            subjEl.value=''; classEl.value='';
        }
        modal.show();
    }
    addBtn.addEventListener('click',()=>openModal('add'));
    saveBtn.addEventListener('click', async ()=>{
        const payload={ date: dateEl.value, subjectName: subjEl.value, className: String(classEl.value), teacherId: Number(teacherEl.value) };
        if(!payload.date||!payload.subjectName||!payload.className||!payload.teacherId){ toast('Uzupełnij wszystkie pola','error'); return; }
        try{
            const id=idEl.value;
            if(id){ await sendJson(`/api/admin/schedule/${id}`,'PUT',payload); toast('Zapisano'); }
            else { await sendJson('/api/admin/schedule','POST',payload); toast('Dodano'); }
            modal.hide(); await load();
        }catch{ toast('Błąd zapisu','error'); }
    });
    await load();
}

/* ===== Frekwencja – Add/Edit/Delete (AttendanceDto shape) ===== */
async function renderAttendance(){
    mountTemplate('tpl-attendance');
    const tbody=document.getElementById('attendanceTbody');
    const addBtn=document.getElementById('attendanceAddBtn');
    const modalEl=document.getElementById('modalAttendance');
    const modal=new bootstrap.Modal(modalEl);
    const title=document.getElementById('attendanceModalTitle');
    const idEl=document.getElementById('attendanceId');
    const dateEl=document.getElementById('attendanceDate');
    const subjEl=document.getElementById('attendanceSubject');
    const studEl=document.getElementById('attendanceStudent');
    const teachEl=document.getElementById('attendanceTeacher');
    const statusEl=document.getElementById('attendanceStatus');
    const saveBtn=document.getElementById('attendanceSaveBtn');

    let data=[], dictSubjects=null, dictStudents=null, dictTeachers=null;
    async function ensureDicts(){
        if(!dictSubjects) dictSubjects=await getJson('/api/admin/subjects');
        if(!dictStudents) dictStudents=await getJson('/api/admin/students');
        if(!dictTeachers) dictTeachers=await getJson('/api/admin/teachers');
        fillSelect(subjEl, dictSubjects, s=>s.name, s=>s.displayName??s.name, true);
        fillSelect(studEl, dictStudents, s=>s.userId, s=>`${s.firstname} ${s.lastname}`, true);
        fillSelect(teachEl, dictTeachers, t=>t.userId, t=>`${t.firstname} ${t.lastname}`, true);
    }
    async function load(){ data=await getJson('/api/admin/attendance'); draw(); }
    function draw(){
        const rows=data.map(a=>{
            const id = a.attendanceId ?? a.id; // kluczowe: użyj właściwego klucza
            return `
      <tr data-id="${id}">
        <td>${a.attendanceDate??''}</td>
        <td>${(a.studentFirstName??'')} ${(a.studentLastName??'')}</td>
        <td>${a.subjectName??''}</td>
        <td>${a.attendanceStatus??''}</td>
        <td>${(a.teacherFirstName??'')} ${(a.teacherLastName??'')}</td>
        <td class="text-nowrap">
          <button class="btn btn-sm btn-outline-secondary me-1 btn-edit"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-outline-danger btn-del"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`;
        }).join('');
        tbody.innerHTML = rows || `<tr><td colspan="6" class="text-center text-secondary">Brak danych</td></tr>`;
        tbody.querySelectorAll('.btn-edit').forEach(b=>b.addEventListener('click',()=>openModal('edit',b.closest('tr').dataset.id)));
        tbody.querySelectorAll('.btn-del').forEach(b=>b.addEventListener('click',()=>delRow(b.closest('tr').dataset.id)));
    }
    async function delRow(id){
        // id pochodzi z data-id ustawionego powyżej i jest attendanceId
        const ok = await confirmModal('Usunąć wpis frekwencji?', 'Usuń frekwencję');
        if(!ok) return;
        try{ await sendJson(`/api/admin/attendance/${id}`,'DELETE'); toast('Usunięto'); await load(); }
        catch{ toast('Błąd usuwania','error'); }
    }
    async function openModal(mode,id=null){
        // 1) najpierw słowniki i opcje selectów
        await ensureDicts();

        if(mode==='add'){
            title.textContent='Dodaj wpis';
            idEl.value=''; dateEl.value=''; subjEl.value=''; studEl.value=''; teachEl.value=''; statusEl.value='';
            modal.show();
            return;
        }

        // 2) pobierz rekord po ID
        title.textContent='Edytuj wpis';
        const a = await getJson(`/api/admin/attendance/${id}`);

        // 3) ustaw hidden ID i datę
        idEl.value = a?.attendanceId ?? '';
        dateEl.value = a?.attendanceDate ?? '';

        // 4) ustaw subject – użyj subjectEnum (kod enuma)
        if (a?.subjectEnum) {
            subjEl.value = String(a.subjectEnum);
        } else {
            // jeśli brak kodu, spróbuj dopasować po display name z listy słowników
            const opt = Array.from(subjEl.options).find(o => o.textContent === (a?.subjectName ?? ''));
            subjEl.value = opt ? opt.value : '';
        }

        // 5) ustaw student/teacher ID
        if (a?.studentId != null) {
            studEl.value = String(a.studentId);
            // jeśli z jakiegoś powodu nie ma opcji z takim value (np. brak w słowniku), niech zostanie pusto
            if (studEl.value !== String(a.studentId)) {
                // spróbuj dopasować po imieniu+nazwisku (opcjonalnie)
                const label = `${a.studentFirstName ?? ''} ${a.studentLastName ?? ''}`.trim();
                const opt = Array.from(studEl.options).find(o => o.textContent.trim() === label);
                if (opt) studEl.value = opt.value;
            }
        } else {
            studEl.value = '';
        }

        if (a?.teacherId != null) {
            teachEl.value = String(a.teacherId);
            if (teachEl.value !== String(a.teacherId)) {
                const label = `${a.teacherFirstName ?? ''} ${a.teacherLastName ?? ''}`.trim();
                const opt = Array.from(teachEl.options).find(o => o.textContent.trim() === label);
                if (opt) teachEl.value = opt.value;
            }
        } else {
            teachEl.value = '';
        }

        // 6) ustaw status – mapuj displayName -> kod enuma
        const displayToCode = {
            'Obecny': 'PRESENT',
            'Nieobecny': 'ABSENT',
            'Spóźnienie': 'LATE',
            'Usprawiedliwione': 'EXCUSED'
        };
        const statusCodes = ['PRESENT','ABSENT','LATE','EXCUSED'];
        let statusCode = a?.attendanceStatus ?? '';
        if (!statusCodes.includes(statusCode)) {
            statusCode = displayToCode[statusCode] ?? '';
        }
        statusEl.value = statusCode;

        modal.show();
    }
    addBtn.addEventListener('click',()=>openModal('add'));
    saveBtn.addEventListener('click', async ()=>{
        const payload = {
            attendanceDate: dateEl.value,
            subjectEnum: subjEl.value,
            studentId: Number(studEl.value),
            teacherId: Number(teachEl.value),
            attendanceStatus: statusEl.value
        };
        if(!payload.attendanceDate||!payload.subjectEnum||!payload.studentId||!payload.teacherId||!payload.attendanceStatus){
            toast('Uzupełnij wszystkie pola','error'); return;
        }
        try{
            const id = idEl.value; // tu już mamy poprawny attendanceId z openModal('edit')
            if(id){
                await sendJson(`/api/admin/attendance/${id}`,'PUT',payload);
                toast('Zapisano');
            }else{
                await sendJson('/api/admin/attendance','POST',payload);
                toast('Dodano');
            }
            modal.hide(); await load();
        }catch{ toast('Błąd zapisu','error'); }
    });
    await load();
}

/* ===== Klasy ===== */
async function renderClasses(){
    mountTemplate('tpl-classes');
    const tbody=document.getElementById('classesTbody');
    const addBtn=document.getElementById('classAddBtn');
    const modalEl=document.getElementById('modalClass');
    const modal=new bootstrap.Modal(modalEl);
    const title=document.getElementById('classModalTitle');
    const idEl=document.getElementById('classId');
    const nameEl=document.getElementById('className');
    const saveBtn=document.getElementById('classSaveBtn');

    let data=[];
    async function load(){ data=await getJson('/api/admin/classes'); draw(); }
    function draw(){
        const rows=(data||[]).map(c=>`
      <tr data-id="${c.schoolClassId??c.id??c.classId}">
        <td>${c.name??''}</td>
        <td>${(c.studentCount != null) ? c.studentCount : (c.students?.length ?? 0)}</td>
        <td class="text-nowrap">
          <button class="btn btn-sm btn-outline-danger btn-del"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`).join('');
        tbody.innerHTML = rows || `<tr><td colspan="3" class="text-center text-secondary">Brak danych</td></tr>`;
        tbody.querySelectorAll('.btn-del').forEach(b=>b.addEventListener('click',()=>delRow(b.closest('tr').dataset.id)));
    }
    async function delRow(id){
        const ok = await confirmModal('Usunąć klasę? Operacja nie powiedzie się, jeśli są powiązane rekordy planu.', 'Usuń klasę');
        if(!ok) return;
        try{ await sendJson(`/api/admin/classes/${id}`,'DELETE'); toast('Usunięto'); await load(); }
        catch(e){ toast('Nie można usunąć klasy – usuń najpierw powiązany plan lub przepnij rekordy.','error'); }
    }
    function openModal(mode,id=null){
        if(mode==='add'){ title.textContent='Dodaj klasę'; idEl.value=''; nameEl.value=''; }
        else{
            title.textContent='Edytuj klasę';
            const c=data.find(x=>String(x.schoolClassId??x.id??x.classId)===String(id));
            idEl.value=c?.schoolClassId??c?.id??c?.classId??''; nameEl.value=c?.name??'';
        }
        modal.show();
    }
    addBtn.addEventListener('click',()=>openModal('add'));
    saveBtn.addEventListener('click', async ()=>{
        const payload={ name:(nameEl.value||'').trim() };
        if(!payload.name){ toast('Nazwa wymagana','error'); return; }
        try{
            if(idEl.value){
                // Brak PUT w backendzie klas — tylko dodawanie i usuwanie. Możemy zasygnalizować:
                toast('Edycja klasy nieobsługiwana w API – zmień nazwę przez usunięcie/dodanie.','error');
            }else{
                await sendJson('/api/admin/classes','POST',payload);
                toast('Dodano'); modal.hide(); await load();
            }
        }catch{ toast('Błąd zapisu','error'); }
    });
    await load();
}

/* ===== Użytkownicy (zostawiamy na później) ===== */
async function renderUsers(){
    mountTemplate('tpl-users');
    const tbody=document.getElementById('usersTbody');
    const addBtn=document.getElementById('userAddBtn');
    const modalEl=document.getElementById('modalUser');
    const modal=new bootstrap.Modal(modalEl);
    const title=document.getElementById('userModalTitle');
    const idEl=document.getElementById('userId');
    const fEl=document.getElementById('userFirst');
    const lEl=document.getElementById('userLast');
    const uEl=document.getElementById('userLogin');
    const eEl=document.getElementById('userEmail');
    const rEl=document.getElementById('userRole');
    const pEl=document.getElementById('userPassword');
    const p2El=document.getElementById('userPassword2');
    const saveBtn=document.getElementById('userSaveBtn');

    let data=[];
    async function load(){ data=await getJson('/api/admin/users'); draw(); }
    function draw(){
        const rows=(data||[]).map(u=>`
      <tr data-id="${u.id}">
        <td>${u.firstname??''}</td>
        <td>${u.lastname??''}</td>
        <td>${u.username??''}</td>
        <td>${u.email??''}</td>
        <td>${u.role??''}</td>
        <td class="text-nowrap">
          <button class="btn btn-sm btn-outline-secondary me-1 btn-edit"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-outline-danger btn-del"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`).join('');
        tbody.innerHTML = rows || `<tr><td colspan="6" class="text-center text-secondary">Brak danych</td></tr>`;
        tbody.querySelectorAll('.btn-edit').forEach(b=>b.addEventListener('click',()=>openModal('edit',b.closest('tr').dataset.id)));
        tbody.querySelectorAll('.btn-del').forEach(b=>b.addEventListener('click',()=>delRow(b.closest('tr').dataset.id)));
    }

    async function delRow(id){
        const ok = await confirmModal('Usunąć użytkownika?', 'Usuń użytkownika');
        if(!ok) return;
        try{ await sendJson(`/api/admin/users/${id}`,'DELETE'); toast('Usunięto'); await load(); }
        catch{ toast('Błąd usuwania','error'); }
    }

    function openModal(mode,id=null){
        if(mode==='add'){
            title.textContent='Dodaj użytkownika';
            idEl.value=''; fEl.value=''; lEl.value=''; uEl.value=''; eEl.value=''; rEl.value=''; pEl.value=''; p2El.value='';
        }else{
            title.textContent='Edytuj użytkownika';
            const u=data.find(x=>String(x.id)===String(id));
            idEl.value=u?.id??''; fEl.value=u?.firstname??''; lEl.value=u?.lastname??''; uEl.value=u?.username??''; eEl.value=u?.email??'';
            const roleMap={ 'Administrator':'ADMIN','Nauczyciel':'TEACHER','Uczeń':'STUDENT','Rodzic':'PARENT' };
            const code=(u?.role||'').toUpperCase();
            rEl.value=['ADMIN','TEACHER','STUDENT','PARENT'].includes(code)?code:(roleMap[u?.role]??'');
            pEl.value=''; p2El.value='';
        }
        modal.show();
    }

    addBtn.addEventListener('click',()=>openModal('add'));

    saveBtn.addEventListener('click', async ()=>{
        // walidacja haseł (jeśli podane)
        if(pEl.value || p2El.value){
            if(pEl.value !== p2El.value){
                toast('Hasła nie są zgodne','error'); return;
            }
            if(pEl.value.length < 6){
                toast('Hasło musi mieć co najmniej 6 znaków','error'); return;
            }
        }

        const payload={
            firstname: fEl.value?.trim(),
            lastname: lEl.value?.trim(),
            username: uEl.value?.trim(),
            email: eEl.value?.trim(),
            role: rEl.value
        };
        // dołącz hasło tylko jeśli ustawione
        if(pEl.value) payload.password = pEl.value;

        if(!payload.firstname||!payload.lastname||!payload.username||!payload.email||!payload.role){
            toast('Uzupełnij wymagane pola','error'); return;
        }
        try{
            const id=idEl.value;
            if(id){
                await sendJson(`/api/admin/users/${id}`,'PUT',payload);
                toast('Zapisano');
            }else{
                await sendJson('/api/admin/users','POST',payload);
                toast('Dodano użytkownika');
            }
            modal.hide(); await load();
        }catch{ toast('Błąd zapisu','error'); }
    });

    await load();
}
