// /js/parent.js
import { toast, escapeHtml } from '/js/common.js';

const $ = (sel) => document.querySelector(sel);

// ====== STAN ======
const state = {
    parentId: null,
    user: null,
    students: [],
    currentStudentId: null,
};

// ====== POMOCNICZE ======
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
    if (tpl?.content) {
        view.appendChild(tpl.content.cloneNode(true));
    }
}

function studentNameById(id) {
    const s = state.students.find((x) => String(x.userId) === String(id));
    return s ? `${s.firstname ?? ''} ${s.lastname ?? ''}`.trim() : '';
}

function setBadge(id, elId) {
    const el = document.getElementById(elId);
    if (el) el.textContent = studentNameById(id);
}

function highlightCurrentChild() {
    document.querySelectorAll('#childrenBox button').forEach((b) => {
        b.classList.toggle('active', String(b.dataset.id) === String(state.currentStudentId));
    });
}

function renderChildren() {
    const box = $('#childrenBox');
    if (!box) return;

    if (!state.students.length) {
        box.innerHTML = `<div class="text-secondary">Brak przypisanych dzieci</div>`;
        return;
    }

    box.innerHTML = state.students
        .map(
            (s) => `
<button type="button"
  class="list-group-item list-group-item-action d-flex align-items-center gap-2"
  data-id="${s.userId}">
  <i class="bi bi-person"></i>
  <span>${escapeHtml(`${s.firstname ?? ''} ${s.lastname ?? ''}`.trim())}</span>
</button>`
        )
        .join('');

    box.querySelectorAll('button').forEach((btn) => {
        btn.addEventListener('click', () => {
            state.currentStudentId = Number(btn.dataset.id);
            highlightCurrentChild();
            handleHash(); // przeładuj widok wg zakładki
        });
    });
}

// ====== ROUTER ======
function handleHash() {
    const view = document.getElementById('view');
    const hash = location.hash || '#schedule';

    if (!state.currentStudentId) {
        if (view) {
            view.innerHTML = `<div class="card-glass p-4 text-secondary">
        Wybierz dziecko po lewej, aby wyświetlić dane.
      </div>`;
        }
        return;
    }

    const routes = {
        '#schedule': renderSchedule,
        '#grades': renderGrades,
        '#attendance': renderAttendance,
        '#remarks': renderRemarks,
    };

    (routes[hash] || routes['#schedule'])();
}

// ====== WIDOKI ======
async function renderSchedule() {
    mountTemplate('tpl-schedule');
    setBadge(state.currentStudentId, 'studentBadgeSchedule');

    const tbody = document.getElementById('scheduleTbody');
    if (!tbody) return;
    tbody.innerHTML = `<tr><td colspan="4" class="text-secondary">Ładowanie...</td></tr>`;

    try {
        const data = await getJson(
            `/api/parent/schedule?studentId=${encodeURIComponent(
                state.currentStudentId
            )}&parentId=${encodeURIComponent(state.parentId)}`
        );

        if (!Array.isArray(data) || !data.length) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-secondary text-center">Brak danych</td></tr>`;
            return;
        }

        tbody.innerHTML = data
            .map((i) => {
                const teacher =
                    (i.teacherName ??
                        `${i.teacherFirstName ?? ''} ${i.teacherLastName ?? ''}`)?.trim() || '';
                return `
<tr>
  <td>${escapeHtml(i.date ?? '')}</td>
  <td>${escapeHtml(i.subjectName ?? i.subject ?? '')}</td>
  <td>${escapeHtml(teacher)}</td>
  <td>${escapeHtml(i.className ?? '')}</td>
</tr>`;
            })
            .join('');
    } catch (e) {
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="4" class="text-danger text-center">Błąd pobierania planu</td></tr>`;
    }
}

async function renderGrades() {
    mountTemplate('tpl-grades');
    setBadge(state.currentStudentId, 'studentBadgeGrades');

    const tbody = document.getElementById('gradesTbody');
    if (!tbody) return;
    tbody.innerHTML = `<tr><td colspan="4" class="text-secondary">Ładowanie...</td></tr>`;

    try {
        const data = await getJson(
            `/api/parent/grades?studentId=${encodeURIComponent(
                state.currentStudentId
            )}&parentId=${encodeURIComponent(state.parentId)}`
        );

        if (!Array.isArray(data) || !data.length) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-secondary text-center">Brak ocen</td></tr>`;
            return;
        }

        tbody.innerHTML = data
            .map((g) => {
                const teacher =
                    `${g.teacherFirstName ?? ''} ${g.teacherLastName ?? ''}`.trim();
                const val =
                    g.value != null
                        ? Number(g.value).toFixed(g.value % 1 === 0 ? 0 : 1)
                        : '';
                return `
<tr>
  <td>${escapeHtml(g.subject ?? g.subjectEnum ?? '')}</td>
  <td>${escapeHtml(val)}</td>
  <td>${escapeHtml(g.gradeDate ?? '')}</td>
  <td>${escapeHtml(teacher)}</td>
</tr>`;
            })
            .join('');
    } catch (e) {
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="4" class="text-danger text-center">Błąd pobierania ocen</td></tr>`;
    }
}

async function renderAttendance() {
    mountTemplate('tpl-attendance');
    setBadge(state.currentStudentId, 'studentBadgeAttendance');

    const tbody = document.getElementById('attendanceTbody');
    if (!tbody) return;
    tbody.innerHTML = `<tr><td colspan="4" class="text-secondary">Ładowanie...</td></tr>`;

    try {
        const data = await getJson(
            `/api/parent/attendance?studentId=${encodeURIComponent(
                state.currentStudentId
            )}&parentId=${encodeURIComponent(state.parentId)}`
        );

        if (!Array.isArray(data) || !data.length) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-secondary text-center">Brak wpisów</td></tr>`;
            return;
        }

        tbody.innerHTML = data
            .map((a) => {
                const teacher =
                    `${a.teacherFirstName ?? ''} ${a.teacherLastName ?? ''}`.trim();
                return `
<tr>
  <td>${escapeHtml(a.date ?? a.attendanceDate ?? '')}</td>
  <td>${escapeHtml(a.subject ?? a.subjectName ?? '')}</td>
  <td>${escapeHtml(a.status ?? a.attendanceStatus ?? '')}</td>
  <td>${escapeHtml(teacher)}</td>
</tr>`;
            })
            .join('');
    } catch (e) {
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="4" class="text-danger text-center">Błąd pobierania frekwencji</td></tr>`;
    }
}

async function renderRemarks() {
    mountTemplate('tpl-remarks');
    setBadge(state.currentStudentId, 'studentBadgeRemarks');

    const tbody = document.getElementById('remarksTbody');
    if (!tbody) return;
    tbody.innerHTML = `<tr><td colspan="3" class="text-secondary">Ładowanie...</td></tr>`;

    try {
        const data = await getJson(
            `/api/parent/remarks/${encodeURIComponent(
                state.currentStudentId
            )}?parentId=${encodeURIComponent(state.parentId)}`
        );

        if (!Array.isArray(data) || !data.length) {
            tbody.innerHTML = `<tr><td colspan="3" class="text-secondary text-center">Brak uwag</td></tr>`;
            return;
        }

        tbody.innerHTML = data
            .map((r) => {
                const teacher =
                    `${r.teacherFirstName ?? ''} ${r.teacherLastName ?? ''}`.trim();
                return `
<tr>
  <td>${escapeHtml(r.addDate ?? '')}</td>
  <td>${escapeHtml(r.content ?? '')}</td>
  <td>${escapeHtml(teacher)}</td>
</tr>`;
            })
            .join('');
    } catch (e) {
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="3" class="text-danger text-center">Błąd pobierania uwag</td></tr>`;
    }
}

// ====== INIT ======
async function init() {
    try {
        // 1) Pobierz zalogowanego użytkownika (parentId)
        const me = await getJson('/api/auth/me');
        if (!me?.userId) {
            toast('Brak autoryzacji', 'error');
            location.href = '/#login';
            return;
        }
        state.user = me;
        state.parentId = me.userId;

        // 2) Pobierz dzieci dla tego rodzica
        const students = await getJson(
            `/api/parent/children?parentId=${encodeURIComponent(state.parentId)}`
        );
        state.students = Array.isArray(students) ? students : [];

        // 3) Render listy dzieci
        renderChildren();

        // 4) Ustaw domyślne dziecko + odśwież widok
        const view = document.getElementById('view');
        if (state.students.length > 0) {
            state.currentStudentId = state.students[0].userId;
            highlightCurrentChild();
            handleHash();
        } else if (view) {
            view.innerHTML = `<div class="card-glass p-4 text-secondary">Brak przypisanych dzieci.</div>`;
        }
    } catch (e) {
        console.error(e);
        toast('Nie udało się pobrać danych rodzica lub dzieci.', 'error');
        const view = document.getElementById('view');
        if (view) {
            view.innerHTML = `<div class="card-glass p-4 text-danger">Błąd inicjalizacji panelu rodzica.</div>`;
        }
    }
}

// ====== LISTENERY ======
window.addEventListener('hashchange', handleHash);

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
} else {
    init();
}
