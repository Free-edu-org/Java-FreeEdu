// /js/student.js
import { toast, escapeHtml } from '/js/common.js';

const $ = (sel) => document.querySelector(sel);

const state = {
    user: null,
    studentId: null,
    profile: null, // dane studenta (do badge'u)
};

async function getJson(url) {
    const r = await fetch(url, { credentials: 'include' });
    if (!r.ok) throw new Error(await r.text().catch(() => r.status));
    return r.json();
}

function mountTemplate(id) {
    const tpl = document.getElementById(id);
    const view = document.getElementById('view');
    if (!view) return;
    view.innerHTML = '';
    if (tpl?.content) view.appendChild(tpl.content.cloneNode(true));
}

function fullName(obj) {
    const first = obj?.firstname ?? obj?.firstName ?? '';
    const last = obj?.lastname ?? obj?.lastName ?? '';
    return `${first} ${last}`.trim();
}

function setBadge(elId) {
    const el = document.getElementById(elId);
    if (!el) return;
    const name = fullName(state.profile ?? state.user ?? {});
    el.textContent = name || 'Uczeń';
}

/* ===== VIEWS ===== */
async function renderSchedule() {
    mountTemplate('tpl-schedule');
    setBadge('studentBadgeSchedule');

    const tbody = document.getElementById('scheduleTbody');
    if (!tbody) return;
    tbody.innerHTML = `<tr><td colspan="4" class="text-secondary">Ładowanie...</td></tr>`;

    try {
        const data = await getJson(`/api/student/schedule?studentId=${encodeURIComponent(state.studentId)}`);
        if (!Array.isArray(data) || !data.length) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-secondary text-center">Brak danych</td></tr>`;
            return;
        }
        tbody.innerHTML = data.map(i => {
            const teacher = (i.teacherName ?? `${i.teacherFirstName ?? ''} ${i.teacherLastName ?? ''}`)?.trim() || '';
            return `
        <tr>
          <td>${escapeHtml(i.date ?? '')}</td>
          <td>${escapeHtml(i.subjectName ?? i.subject ?? '')}</td>
          <td>${escapeHtml(teacher)}</td>
          <td>${escapeHtml(i.className ?? '')}</td>
        </tr>`;
        }).join('');
    } catch (e) {
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="4" class="text-danger text-center">Błąd pobierania planu</td></tr>`;
    }
}

async function renderGrades() {
    mountTemplate('tpl-grades');
    setBadge('studentBadgeGrades');

    const tbody = document.getElementById('gradesTbody');
    if (!tbody) return;
    tbody.innerHTML = `<tr><td colspan="4" class="text-secondary">Ładowanie...</td></tr>`;

    try {
        const data = await getJson(`/api/student/grades?studentId=${encodeURIComponent(state.studentId)}`);
        if (!Array.isArray(data) || !data.length) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-secondary text-center">Brak ocen</td></tr>`;
            return;
        }
        tbody.innerHTML = data.map(g => {
            const teacher = `${g.teacherFirstName ?? ''} ${g.teacherLastName ?? ''}`.trim();
            const val = g.value != null ? Number(g.value).toFixed(g.value % 1 === 0 ? 0 : 1) : '';
            return `
        <tr>
          <td>${escapeHtml(g.subject ?? g.subjectEnum ?? '')}</td>
          <td>${escapeHtml(val)}</td>
          <td>${escapeHtml(g.gradeDate ?? '')}</td>
          <td>${escapeHtml(teacher)}</td>
        </tr>`;
        }).join('');
    } catch (e) {
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="4" class="text-danger text-center">Błąd pobierania ocen</td></tr>`;
    }
}

async function renderAttendance() {
    mountTemplate('tpl-attendance');
    setBadge('studentBadgeAttendance');

    const tbody = document.getElementById('attendanceTbody');
    if (!tbody) return;
    tbody.innerHTML = `<tr><td colspan="4" class="text-secondary">Ładowanie...</td></tr>`;

    try {
        const data = await getJson(`/api/student/attendance?studentId=${encodeURIComponent(state.studentId)}`);
        if (!Array.isArray(data) || !data.length) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-secondary text-center">Brak wpisów</td></tr>`;
            return;
        }
        tbody.innerHTML = data.map(a => {
            const teacher = `${a.teacherFirstName ?? ''} ${a.teacherLastName ?? ''}`.trim();
            return `
        <tr>
          <td>${escapeHtml(a.date ?? a.attendanceDate ?? '')}</td>
          <td>${escapeHtml(a.subject ?? a.subjectName ?? '')}</td>
          <td>${escapeHtml(a.status ?? a.attendanceStatus ?? '')}</td>
          <td>${escapeHtml(teacher)}</td>
        </tr>`;
        }).join('');
    } catch (e) {
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="4" class="text-danger text-center">Błąd pobierania frekwencji</td></tr>`;
    }
}

async function renderRemarks() {
    mountTemplate('tpl-remarks');
    setBadge('studentBadgeRemarks');

    const tbody = document.getElementById('remarksTbody');
    if (!tbody) return;
    tbody.innerHTML = `<tr><td colspan="3" class="text-secondary">Ładowanie...</td></tr>`;

    try {
        const data = await getJson(`/api/student/remarks?studentId=${encodeURIComponent(state.studentId)}`);
        if (!Array.isArray(data) || !data.length) {
            tbody.innerHTML = `<tr><td colspan="3" class="text-secondary text-center">Brak uwag</td></tr>`;
            return;
        }
        tbody.innerHTML = data.map(r => {
            const teacher = `${r.teacherFirstName ?? ''} ${r.teacherLastName ?? ''}`.trim();
            return `
        <tr>
          <td>${escapeHtml(r.addDate ?? '')}</td>
          <td>${escapeHtml(r.content ?? '')}</td>
          <td>${escapeHtml(teacher)}</td>
        </tr>`;
        }).join('');
    } catch (e) {
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="3" class="text-danger text-center">Błąd pobierania uwag</td></tr>`;
    }
}

/* ===== Router ===== */
const routes = {
    '#schedule': renderSchedule,
    '#grades': renderGrades,
    '#attendance': renderAttendance,
    '#remarks': renderRemarks,
};

function handleHash() {
    const hash = location.hash || '#schedule';
    (routes[hash] || routes['#schedule'])();
}

/* ===== Init ===== */
async function init() {
    try {
        // 1) me -> studentId
        const me = await getJson('/api/auth/me');
        if (!me?.userId) {
            toast('Brak autoryzacji', 'error');
            location.href = '/#login';
            return;
        }
        state.user = me;
        state.studentId = me.userId;

        // 2) profil do badge'u (opcjonalnie)
        try {
            state.profile = await getJson(`/api/student/profile?studentId=${encodeURIComponent(state.studentId)}`);
        } catch {
            state.profile = null; // nie blokuj widoku, jeśli profil się nie powiedzie
        }

        // 3) start
        handleHash();
    } catch (e) {
        console.error(e);
        const view = document.getElementById('view');
        if (view) {
            view.innerHTML = `<div class="card-glass p-4 text-danger">Błąd inicjalizacji panelu ucznia.</div>`;
        }
    }
}

/* listeners */
window.addEventListener('hashchange', handleHash);

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
} else {
    init();
}
