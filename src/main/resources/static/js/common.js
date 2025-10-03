// /js/common.js

/* ---------- Helpers ---------- */
export function escapeHtml(s) {
    return String(s ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

export async function getJson(url, opts = {}) {
    try {
        const r = await fetch(url, { credentials: 'include', ...opts });
        if (!r.ok) return null;
        const ct = r.headers.get('content-type') || '';
        if (!ct.includes('application/json')) return null;
        return await r.json();
    } catch {
        return null;
    }
}

/* ---------- User banner ---------- */
export async function renderUserBanner(opts = {}) {
    const { hideAuthLinks = false } = opts;
    const el = document.getElementById('userBanner');
    if (!el) return;

    const me = await getJson('/api/auth/me');

    // mapujemy obie konwencje: ROLE_ADMIN oraz ADMIN
    const roleToHref = {
        ROLE_ADMIN:   '/admin/index.html',
        ROLE_TEACHER: '/teacher/index.html',
        ROLE_PARENT:  '/parent/index.html',
        ROLE_STUDENT: '/student/index.html',
        ADMIN:   '/admin/index.html',
        TEACHER: '/teacher/index.html',
        PARENT:  '/parent/index.html',
        STUDENT: '/student/index.html'
    };

    if (me?.username) {
        const uname = escapeHtml(me.username);
        el.innerHTML = `
      <span class="d-inline-flex align-items-center gap-1">👋 <strong>${uname}</strong></span>
      <button id="gotoPanel" class="btn btn-sm btn-outline-secondary" type="button">Panel</button>
      <button id="logoutBtn" class="btn btn-sm btn-ghost" type="button">Wyloguj</button>
    `;

        document.getElementById('logoutBtn')?.addEventListener('click', async () => {
            try { await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' }); }
            finally { location.href = '/'; }
        });

        document.getElementById('gotoPanel')?.addEventListener('click', () => {
            const dest = roleToHref[me.role] ?? '/';
            location.href = dest;
        });
    } else {
        el.innerHTML = hideAuthLinks
            ? ''
            : `
      <a class="btn btn-sm btn-ghost" href="/#auth">Zaloguj</a>
      <a class="btn btn-sm btn-outline-secondary" href="/#auth">Rejestracja</a>
    `;
    }
}

/* ---------- Toasts (aria-live) ---------- */
export function toast(msg, type = 'success') {
    let wrap = document.getElementById('toasts');
    if (!wrap) {
        wrap = document.createElement('div');
        wrap.id = 'toasts';
        // dostępność: czytniki ekranu ogłoszą komunikat
        wrap.setAttribute('role', 'status');
        wrap.setAttribute('aria-live', 'polite');
        document.body.appendChild(wrap);
    }

    const item = document.createElement('div');
    item.className = `toast-item ${type === 'error' ? 'toast-error' : 'toast-success'}`;
    item.textContent = String(msg ?? '');
    wrap.appendChild(item);

    // auto-dismissing
    window.setTimeout(() => item.remove(), 2500);
}

/* ---------- Theme toggle ---------- */
export function initThemeToggle() {
    const btn = document.getElementById('themeToggle');
    if (!btn) return;

    const apply = (theme) => document.documentElement.setAttribute('data-bs-theme', theme);
    const label = (theme) => (theme === 'dark' ? '☀️ jasny' : '🌙 ciemny');

    const saved = localStorage.getItem('theme') || 'dark';
    apply(saved);
    btn.textContent = label(saved);

    btn.addEventListener('click', () => {
        const cur = document.documentElement.getAttribute('data-bs-theme') || 'dark';
        const next = cur === 'dark' ? 'light' : 'dark';
        apply(next);
        localStorage.setItem('theme', next);
        btn.textContent = label(next);
    });
}
