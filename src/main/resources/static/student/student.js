// /js/student.js
import { toast, escapeHtml } from '../js/common.js';

const $  = (sel, root=document) => root.querySelector(sel);
const $$ = (sel, root=document) => [...root.querySelectorAll(sel)];

// ===== STATE =====
const state = {
    user: null,
    studentId: null,
    profile: null, // tylko do badge’u
};

// ===== UTILS =====
async function getJson(url){
    const r = await fetch(url, { credentials: 'include' });
    if(!r.ok) throw new Error(await r.text().catch(()=>r.status));
    return r.json();
}
function mountTemplate(id){
    const tpl = document.getElementById(id);
    const view = document.getElementById('view');
    if(!tpl?.content || !view) return;
    view.innerHTML = '';
    view.appendChild(tpl.content.cloneNode(true));
}
function fullName(obj){
    const first = obj?.firstname ?? obj?.firstName ?? '';
    const last  = obj?.lastname  ?? obj?.lastName  ?? '';
    return `${first} ${last}`.trim();
}
function setBadge(elId){
    const el = document.getElementById(elId);
    if(!el) return;
    el.textContent = fullName(state.profile ?? state.user ?? {}) || 'Uczeń';
}

// ===== VIEWS =====
async function renderSchedule(){
    mountTemplate('tpl-schedule');
    setBadge('studentBadgeSchedule');

    const tbody = $('#scheduleTbody');
    if(!tbody) return;
    tbody.innerHTML = `<tr><td colspan="4" class="text-secondary">Ładowanie...</td></tr>`;

    try{
        const data = await getJson(`/api/student/schedule?studentId=${encodeURIComponent(state.studentId)}`);
        if(!Array.isArray(data) || data.length === 0){
            tbody.innerHTML = `<tr><td colspan="4" class="text-secondary text-center">Brak danych</td></tr>`;
            return;
        }
        tbody.innerHTML = data.map(i=>{
            const teacher = (i.teacherName ?? `${i.teacherFirstName ?? ''} ${i.teacherLastName ?? ''}`)?.trim() || '';
            return `<tr>
        <td>${escapeHtml(i.date ?? '')}</td>
        <td>${escapeHtml(i.subjectName ?? i.subject ?? '')}</td>
        <td>${escapeHtml(teacher)}</td>
        <td>${escapeHtml(i.className ?? '')}</td>
      </tr>`;
        }).join('');
    }catch(e){
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="4" class="text-danger text-center">Błąd pobierania planu</td></tr>`;
    }
}

async function renderGrades(){
    mountTemplate('tpl-grades');
    setBadge('studentBadgeGrades');

    const tbody = $('#gradesTbody');
    if(!tbody) return;
    tbody.innerHTML = `<tr><td colspan="4" class="text-secondary">Ładowanie...</td></tr>`;

    try{
        const data = await getJson(`/api/student/grades?studentId=${encodeURIComponent(state.studentId)}`);
        if(!Array.isArray(data) || data.length === 0){
            tbody.innerHTML = `<tr><td colspan="4" class="text-secondary text-center">Brak ocen</td></tr>`;
            return;
        }
        tbody.innerHTML = data.map(g=>{
            const teacher = `${g.teacherFirstName ?? ''} ${g.teacherLastName ?? ''}`.trim();
            const val = g.value != null ? Number(g.value).toFixed(g.value % 1 === 0 ? 0 : 1) : '';
            return `<tr>
        <td>${escapeHtml(g.subject ?? g.subjectEnum ?? '')}</td>
        <td>${escapeHtml(val)}</td>
        <td>${escapeHtml(g.gradeDate ?? '')}</td>
        <td>${escapeHtml(teacher)}</td>
      </tr>`;
        }).join('');
    }catch(e){
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="4" class="text-danger text-center">Błąd pobierania ocen</td></tr>`;
    }
}

async function renderAttendance(){
    mountTemplate('tpl-attendance');
    setBadge('studentBadgeAttendance');

    const tbody = $('#attendanceTbody');
    if(!tbody) return;
    tbody.innerHTML = `<tr><td colspan="4" class="text-secondary">Ładowanie...</td></tr>`;

    try{
        const data = await getJson(`/api/student/attendance?studentId=${encodeURIComponent(state.studentId)}`);
        if(!Array.isArray(data) || data.length === 0){
            tbody.innerHTML = `<tr><td colspan="4" class="text-secondary text-center">Brak wpisów</td></tr>`;
            return;
        }
        tbody.innerHTML = data.map(a=>{
            const teacher = `${a.teacherFirstName ?? ''} ${a.teacherLastName ?? ''}`.trim();
            return `<tr>
        <td>${escapeHtml(a.date ?? a.attendanceDate ?? '')}</td>
        <td>${escapeHtml(a.subject ?? a.subjectName ?? '')}</td>
        <td>${escapeHtml(a.status ?? a.attendanceStatus ?? '')}</td>
        <td>${escapeHtml(teacher)}</td>
      </tr>`;
        }).join('');
    }catch(e){
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="4" class="text-danger text-center">Błąd pobierania frekwencji</td></tr>`;
    }
}

async function renderRemarks(){
    mountTemplate('tpl-remarks');
    setBadge('studentBadgeRemarks');

    const tbody = $('#remarksTbody');
    if(!tbody) return;
    tbody.innerHTML = `<tr><td colspan="3" class="text-secondary">Ładowanie...</td></tr>`;

    try{
        const data = await getJson(`/api/student/remarks?studentId=${encodeURIComponent(state.studentId)}`);
        if(!Array.isArray(data) || data.length === 0){
            tbody.innerHTML = `<tr><td colspan="3" class="text-secondary text-center">Brak uwag</td></tr>`;
            return;
        }
        tbody.innerHTML = data.map(r=>{
            const teacher = `${r.teacherFirstName ?? ''} ${r.teacherLastName ?? ''}`.trim();
            return `<tr>
        <td>${escapeHtml(r.addDate ?? '')}</td>
        <td>${escapeHtml(r.content ?? '')}</td>
        <td>${escapeHtml(teacher)}</td>
      </tr>`;
        }).join('');
    }catch(e){
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="3" class="text-danger text-center">Błąd pobierania uwag</td></tr>`;
    }
}

// ===== ROUTER =====
const routes = {
    '#schedule'  : renderSchedule,
    '#grades'    : renderGrades,
    '#attendance': renderAttendance,
    '#remarks'   : renderRemarks,
};
function handleHash(){
    const hash = location.hash || '#schedule';
    (routes[hash] || routes['#schedule'])();
}

// ===== INIT =====
async function init(){
    try{
        const me = await getJson('/api/auth/me');
        if(!me?.userId){
            toast('Brak autoryzacji','error');
            location.href = '/#login';
            return;
        }
        state.user = me;
        state.studentId = me.userId;

        try{
            state.profile = await getJson(`/api/student/profile?studentId=${encodeURIComponent(state.studentId)}`);
        }catch{
            state.profile = null;
        }

        handleHash();
    }catch(e){
        console.error(e);
        const view = document.getElementById('view');
        if(view){
            view.innerHTML = `<div class="card-glass p-4 text-danger">Błąd inicjalizacji panelu ucznia.</div>`;
        }
    }
}

// listeners
window.addEventListener('hashchange', handleHash);

if(document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', () => { init().catch(console.error); });
}else{
    init().catch(console.error);
}
