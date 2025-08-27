async function getJson(url){
    const r = await fetch(url,{credentials:'include'});
    if(!r.ok) return null;
    try { return await r.json(); } catch { return null; }
}

export async function renderUserBanner(opts = {}){
    const { hideAuthLinks = false } = opts;
    const el = document.getElementById('userBanner');
    if(!el) return;

    const me = await getJson('/api/auth/me');
    const esc = s => String(s ?? '')
        .replace(/&/g,'&amp;').replace(/</g,'&lt;')
        .replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#39;');

    if(me?.username){
        el.innerHTML = `
      <span class="d-inline-flex align-items-center gap-1">👋 <strong>${esc(me.username)}</strong></span>
      <button id="gotoPanel" class="btn btn-sm btn-outline-secondary">Panel</button>
      <button id="logoutBtn" class="btn btn-sm btn-ghost">Wyloguj</button>
    `;

        document.getElementById('logoutBtn')?.addEventListener('click', async ()=>{
            await fetch('/api/auth/logout',{method:'POST',credentials:'include'});
            location.href='/';
        });
        document.getElementById('gotoPanel')?.addEventListener('click', ()=>{
            switch(me.role){
                case 'ROLE_ADMIN':   location.href='/admin/index.html'; break;
                case 'ROLE_TEACHER': location.href='/view/teacher/mainpage'; break;
                case 'ROLE_PARENT':  location.href='/view/parent/mainpage'; break;
                case 'ROLE_STUDENT': location.href='/view/student/mainpage'; break;
                default: location.href='/';
            }
        });
    } else {
        el.innerHTML = hideAuthLinks ? '' :
            `<a class="btn btn-sm btn-ghost" href="/#auth">Zaloguj</a>
       <a class="btn btn-sm btn-outline-secondary" href="/#auth">Rejestracja</a>`;
    }
}

export function toast(msg,type='success'){
    let wrap = document.getElementById('toasts');
    if(!wrap){ wrap = document.createElement('div'); wrap.id='toasts'; document.body.appendChild(wrap); }
    const item = document.createElement('div');
    item.className = `toast-item ${type==='error'?'toast-error':'toast-success'}`;
    item.textContent = msg;
    wrap.appendChild(item);
    setTimeout(()=> item.remove(), 2500);
}

export function initThemeToggle(){
    const btn = document.getElementById('themeToggle');
    if(!btn) return;
    const apply = t => document.documentElement.setAttribute('data-bs-theme', t);
    const saved = localStorage.getItem('theme') || 'dark';
    apply(saved);
    btn.textContent = saved==='dark' ? '☀️ jasny' : '🌙 ciemny';
    btn.addEventListener('click', ()=>{
        const cur = document.documentElement.getAttribute('data-bs-theme') || 'dark';
        const next = (cur==='dark')?'light':'dark';
        apply(next); localStorage.setItem('theme', next);
        btn.textContent = next==='dark' ? '☀️ jasny' : '🌙 ciemny';
    });
}
