import { toast } from '/js/common.js';

const $ = (sel, root = document) => root.querySelector(sel);

const loginForm    = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const tabLogin     = document.getElementById('tabLogin');
const tabRegister  = document.getElementById('tabRegister');
const authSection  = document.getElementById('auth');
const btnStart     = document.getElementById('btnStart');

function expandAuth()  { authSection?.classList.replace('auth-collapsed','auth-expanded'); }
function collapseAuth(){ authSection?.classList.replace('auth-expanded','auth-collapsed'); }

function show(view, { expand = false, setHash = false } = {}) {
    const isLogin = view === 'login';
    loginForm?.classList.toggle('d-none', !isLogin);
    registerForm?.classList.toggle('d-none', isLogin);
    tabLogin?.classList.toggle('btn-primary', isLogin);
    tabLogin?.classList.toggle('btn-ghost',  !isLogin);
    tabRegister?.classList.toggle('btn-primary', !isLogin);
    tabRegister?.classList.toggle('btn-ghost',  isLogin);
    if (setHash) location.hash = isLogin ? '#login' : '#register';
    localStorage.setItem('authTab', view);
    if (expand) expandAuth();
}

function handleHash() {
    const h = location.hash || '#start';
    if (h === '#login')      { show('login',    { expand: true }); }
    else if (h === '#register'){ show('register',{ expand: true }); }
    else {
        collapseAuth();
        show(localStorage.getItem('authTab') || 'login');
    }
}
window.addEventListener('hashchange', handleHash);
handleHash();

tabLogin?.addEventListener('click',   () => show('login',    { expand: true, setHash: true }));
tabRegister?.addEventListener('click',() => show('register', { expand: true, setHash: true }));

btnStart?.addEventListener('click', () => {
    show('login', { expand: true, setHash: true });
    authSection?.scrollIntoView({ behavior: 'smooth', block: 'start' });
});

document.querySelectorAll('a[href="#start"]').forEach(a =>
    a.addEventListener('click', () => collapseAuth())
);

document.querySelectorAll('[data-toggle="password"]').forEach(btn => {
    btn.addEventListener('click', () => {
        const id  = btn.getAttribute('data-target');
        const inp = id ? document.getElementById(id) : null;
        if (!inp) return;
        const toType = inp.type === 'password' ? 'text' : 'password';
        inp.type = toType;
        btn.textContent = toType === 'password' ? 'Pokaż' : 'Ukryj';
    });
});

async function postJson(url, data) {
    const r = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(data ?? {})
    });
    if (!r.ok) throw new Error(await r.text().catch(() => ''));
    const ct = r.headers.get('content-type') || '';
    if (!ct.includes('application/json')) return {};
    try { return await r.json(); } catch { return {}; }
}

async function me() {
    try {
        const r = await fetch('/api/auth/me', { credentials: 'include' });
        if (!r.ok) return null;
        const ct = r.headers.get('content-type') || '';
        if (!ct.includes('application/json')) return null;
        return await r.json();
    } catch {
        return null;
    }
}

const RoleMap = {
    ROLE_ADMIN:   '/admin/index.html',
    ROLE_TEACHER: '/teacher/index.html',
    ROLE_PARENT:  '/parent/index.html',
    ROLE_STUDENT: '/student/index.html',
    ADMIN:   '/admin/index.html',
    TEACHER: '/teacher/index.html',
    PARENT:  '/parent/index.html',
    STUDENT: '/student/index.html',
};

function isKnownRole(role){
    return role && Object.prototype.hasOwnProperty.call(RoleMap, role);
}

async function handleUnknownRole() {
    try { await fetch('/api/auth/logout', { method:'POST', credentials:'include' }); } catch {}
    const err = document.getElementById('loginError');
    if (err) {
        err.textContent = 'Twoje konto nie ma przypisanej roli. Skontaktuj się z administratorem, aby przypisać odpowiednią rolę.';
        err.classList.remove('d-none');
    }
    toast('Brak przypisanej roli. Skontaktuj się z administratorem.', 'error');
    show('login', { expand: true, setHash: true });
}

async function redirectByRole() {
    const u = await me();
    const role = u?.role;
    if (!isKnownRole(role)) {
        await handleUnknownRole();
        return;
    }
    location.href = RoleMap[role];
}

if (loginForm) {
    const submitBtn = loginForm.querySelector('button[type="submit"]');
    let busy = false;
    loginForm.addEventListener('submit', async e => {
        e.preventDefault();
        if (busy) return;
        busy = true;
        const fd = new FormData(loginForm);
        const username = fd.get('username');
        const password = fd.get('password');
        const err = document.getElementById('loginError');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>Logowanie...`;
        }
        try {
            await postJson('/api/auth/login', { username, password });
            await redirectByRole();
        } catch {
            err?.classList.remove('d-none');
            err && (err.textContent = 'Błędny login lub hasło.');
            toast('Błędny login lub hasło', 'error');
        } finally {
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Zaloguj się';
            }
            busy = false;
        }
    });
}

if (registerForm) {
    const pass  = document.getElementById('r_password');
    const pass2 = document.getElementById('r_confirm');
    const box   = document.getElementById('strengthBox');
    const bar   = document.getElementById('strengthBar');
    const msg   = document.getElementById('registerMsg');

    const strength = (pwd) => {
        if (!pwd) return 0;
        let score = 0;
        if (pwd.length >= 8) score++;
        if (/[A-Z]/.test(pwd)) score++;
        if (/[a-z]/.test(pwd)) score++;
        if (/[0-9]/.test(pwd)) score++;
        if (/[^A-Za-z0-9]/.test(pwd)) score++;
        return Math.min(score, 4);
    };

    function renderStrength() {
        const v   = strength(pass?.value ?? '');
        const pct = [0, 25, 50, 75, 100][v];
        if (bar) bar.style.width = pct + '%';
        if (box) {
            box.classList.toggle('good',   v >= 2);
            box.classList.toggle('strong', v >= 3);
        }
    }

    pass?.addEventListener('input', renderStrength);
    pass2?.addEventListener('input', () => document.getElementById('passwordError')?.classList.add('d-none'));
    renderStrength();

    registerForm.addEventListener('submit', async e => {
        e.preventDefault();
        const pwd  = pass?.value ?? '';
        const pwd2 = pass2?.value ?? '';
        if (pwd !== pwd2) {
            document.getElementById('passwordError')?.classList.remove('d-none');
            toast('Hasła muszą być identyczne', 'error');
            return;
        }
        const fd = new FormData(registerForm);
        const payload = Object.fromEntries(fd.entries());
        try {
            await postJson('/api/auth/register', payload);
            if (msg) {
                msg.className = 'alert alert-success py-2';
                msg.textContent = 'Konto utworzone. Zaloguj się.';
                msg.classList.remove('d-none');
            }
            show('login', { expand: true, setHash: true });
        } catch {
            if (msg) {
                msg.className = 'alert alert-danger py-2';
                msg.textContent = 'Rejestracja nieudana.';
                msg.classList.remove('d-none');
            }
            toast('Rejestracja nieudana', 'error');
        }
    });
}
