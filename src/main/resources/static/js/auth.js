import { toast } from '/js/common.js';

const loginForm    = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const tabLogin     = document.getElementById('tabLogin');
const tabRegister  = document.getElementById('tabRegister');
const authSection  = document.getElementById('auth');
const btnStart     = document.getElementById('btnStart');

function expandAuth(){ authSection?.classList.replace('auth-collapsed','auth-expanded'); }
function collapseAuth(){ authSection?.classList.replace('auth-expanded','auth-collapsed'); }

function show(view,{expand=false,setHash=false}={}){
    const isLogin = view==='login';
    loginForm.classList.toggle('d-none', !isLogin);
    registerForm.classList.toggle('d-none', isLogin);
    tabLogin.classList.toggle('btn-primary', isLogin);
    tabLogin.classList.toggle('btn-ghost', !isLogin);
    tabRegister.classList.toggle('btn-primary', !isLogin);
    tabRegister.classList.toggle('btn-ghost', isLogin);
    if(setHash) location.hash = isLogin ? '#login' : '#register';
    localStorage.setItem('authTab', view);
    if(expand) expandAuth();
}

/* Hash routing */
function handleHash(){
    const h = location.hash || '#start';
    if(h==='#login'){ show('login',{expand:true}); }
    else if(h==='#register'){ show('register',{expand:true}); }
    else { collapseAuth(); show(localStorage.getItem('authTab')||'login'); }
}
window.addEventListener('hashchange', handleHash);
handleHash();

/* Tabs */
tabLogin?.addEventListener('click', ()=> show('login',{expand:true,setHash:true}));
tabRegister?.addEventListener('click', ()=> show('register',{expand:true,setHash:true}));

/* CTA */
btnStart?.addEventListener('click', ()=>{
    show('login',{expand:true,setHash:true});
    authSection?.scrollIntoView({behavior:'smooth', block:'start'});
});

/* Back links */
document.querySelectorAll('a[href="#start"]').forEach(a=> a.addEventListener('click', ()=> collapseAuth()));

/* Toggle password */
document.querySelectorAll('[data-toggle="password"]').forEach(btn=>{
    btn.addEventListener('click', ()=>{
        const id = btn.getAttribute('data-target'); const inp = document.getElementById(id);
        if(!inp) return; inp.type = (inp.type==='password')?'text':'password';
        btn.textContent = (inp.type==='password')?'Pokaż':'Ukryj';
    });
});

/* API */
async function postJson(url,data){
    const r = await fetch(url,{method:'POST',headers:{'Content-Type':'application/json'},credentials:'include',body:JSON.stringify(data)});
    if(!r.ok) throw new Error(await r.text().catch(()=>'')); return r.json().catch(()=> ({}));
}
async function me(){
    const r = await fetch('/api/auth/me',{credentials:'include'}); if(!r.ok) return null; return r.json();
}
async function redirectByRole(){
    const u = await me(); if(!u?.role){ location.href='/#login'; return; }
    switch(u.role){
        case 'ROLE_ADMIN':   location.href='/view/admin/mainpage'; break;
        case 'ROLE_TEACHER': location.href='/view/teacher/mainpage'; break;
        case 'ROLE_PARENT':  location.href='/view/parent/mainpage'; break;
        case 'ROLE_STUDENT': location.href='/view/student/mainpage'; break;
        default: location.href='/';
    }
}

/* Login */
if(loginForm){
    const submitBtn = loginForm.querySelector('button[type="submit"]');
    loginForm.addEventListener('submit', async e=>{
        e.preventDefault();
        const fd = new FormData(loginForm);
        const username = fd.get('username'); const password = fd.get('password');
        const err = document.getElementById('loginError');
        submitBtn.disabled = true;
        submitBtn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>Logowanie...`;
        try{ await postJson('/api/auth/login',{username,password}); toast('Zalogowano ✅'); await redirectByRole(); }
        catch{ err?.classList.remove('d-none'); toast('Błędny login lub hasło','error'); submitBtn.disabled=false; submitBtn.textContent='Zaloguj się'; }
    });
}

/* Register */
if(registerForm){
    const pass = document.getElementById('r_password');
    const pass2 = document.getElementById('r_confirm');
    const box = document.getElementById('strengthBox');
    const bar = document.getElementById('strengthBar');
    const msg = document.getElementById('registerMsg');

    const strength = pwd => [0,1,2,3,4].slice(0, 5 && (
        (pwd.length>=8) + /[A-Z]/.test(pwd) + /[a-z]/.test(pwd) + /[0-9]/.test(pwd) + /[^A-Za-z0-9]/.test(pwd)
    )).length; // 0..4

    function renderStrength(){
        const v = strength(pass.value); const pct = [0,25,50,75,100][v];
        bar.style.width = pct+'%'; box.classList.toggle('good', v>=2); box.classList.toggle('strong', v>=3);
    }
    pass.addEventListener('input', renderStrength);
    pass2.addEventListener('input', ()=> document.getElementById('passwordError')?.classList.add('d-none'));
    renderStrength();

    registerForm.addEventListener('submit', async e=>{
        e.preventDefault();
        if(pass.value !== pass2.value){
            document.getElementById('passwordError')?.classList.remove('d-none'); toast('Hasła muszą być identyczne','error'); return;
        }
        const fd = new FormData(registerForm); const payload = Object.fromEntries(fd.entries());
        try{
            await postJson('/api/auth/register', payload);
            msg.className='alert alert-success py-2'; msg.textContent='Konto utworzone. Zaloguj się.'; msg.classList.remove('d-none');
            toast('Rejestracja OK ✅'); show('login',{expand:true,setHash:true});
        }catch{
            msg.className='alert alert-danger py-2'; msg.textContent='Rejestracja nieudana.'; msg.classList.remove('d-none');
            toast('Rejestracja nieudana','error');
        }
    });
}
