function getToken() { return localStorage.getItem('jwt_token'); }
function isLoggedIn() { return !!getToken(); }
function logout() { localStorage.removeItem('jwt_token'); window.location.href = '/login'; }

async function apiGet(path) {
    const res = await fetch('/api' + path, { headers: { 'Authorization': 'Bearer ' + getToken() } });
    if (res.status === 401 || res.status === 403) { logout(); return null; }
    if (!res.ok) return null;
    return res.json();
}

async function apiPost(path, body) {
    const res = await fetch('/api' + path, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + getToken() },
        body: JSON.stringify(body)
    });
    if (!res.ok) { const e = await res.json(); alert(e.error || 'Error'); return null; }
    return res.json();
}

async function apiPut(path, body) {
    const res = await fetch('/api' + path, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + getToken() },
        body: JSON.stringify(body)
    });
    if (!res.ok) { const e = await res.json(); alert(e.error || 'Error'); return null; }
    return res.json();
}

async function apiDelete(path) {
    const res = await fetch('/api' + path, { method: 'DELETE', headers: { 'Authorization': 'Bearer ' + getToken() } });
    return res.ok;
}

function formatCurrency(v) { return v != null ? '₹' + Number(v).toLocaleString('en-IN') : '₹0'; }
