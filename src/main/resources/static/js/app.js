const API_BASE = '/api';

function getToken() {
    return localStorage.getItem('jwt_token');
}

function setToken(token) {
    localStorage.setItem('jwt_token', token);
}

function clearToken() {
    localStorage.removeItem('jwt_token');
}

function isLoggedIn() {
    return !!getToken();
}

function logout() {
    clearToken();
    window.location.href = '/login';
}

async function apiGet(path) {
    try {
        const res = await fetch(API_BASE + path, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        if (res.status === 401 || res.status === 403) {
            clearToken();
            window.location.href = '/login';
            return null;
        }
        if (!res.ok) return null;
        return await res.json();
    } catch (e) {
        return null;
    }
}

async function apiPost(path, body) {
    try {
        const res = await fetch(API_BASE + path, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getToken()
            },
            body: JSON.stringify(body)
        });
        if (!res.ok) {
            const err = await res.json();
            alert(err.error || 'Request failed');
            return null;
        }
        return await res.json();
    } catch (e) {
        alert('Connection failed');
        return null;
    }
}

async function apiPut(path, body) {
    try {
        const res = await fetch(API_BASE + path, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getToken()
            },
            body: JSON.stringify(body)
        });
        if (!res.ok) {
            const err = await res.json();
            alert(err.error || 'Request failed');
            return null;
        }
        return await res.json();
    } catch (e) {
        alert('Connection failed');
        return null;
    }
}

async function apiDelete(path) {
    try {
        const res = await fetch(API_BASE + path, {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        return res.ok;
    } catch (e) {
        return false;
    }
}

function formatCurrency(val) {
    if (val == null) return '₹0';
    return '₹' + Number(val).toLocaleString('en-IN', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
}

function formatNumber(val) {
    if (val == null) return '0';
    return Number(val).toLocaleString('en-IN');
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

function setActiveNav() {
    const path = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === path) {
            link.classList.add('active');
        }
    });
}

function showLoading(containerId) {
    const el = document.getElementById(containerId);
    if (el) el.innerHTML = '<div class="loading"><div class="spinner"></div>Loading...</div>';
}

function showEmpty(containerId, message) {
    const el = document.getElementById(containerId);
    if (el) el.innerHTML = '<div class="empty-state"><p>' + message + '</p></div>';
}

function getVelocityBadge(rank) {
    const map = {
        'FAST': '<span class="badge badge-green">Fast</span>',
        'MEDIUM': '<span class="badge badge-blue">Medium</span>',
        'SLOW': '<span class="badge badge-orange">Slow</span>',
        'DEAD': '<span class="badge badge-red">Dead</span>'
    };
    return map[rank] || rank;
}

function getAbcBadge(cls) {
    const map = {
        'A': '<span class="badge badge-red">A</span>',
        'B': '<span class="badge badge-orange">B</span>',
        'C': '<span class="badge badge-gray">C</span>'
    };
    return map[cls] || cls;
}

document.addEventListener('DOMContentLoaded', setActiveNav);
