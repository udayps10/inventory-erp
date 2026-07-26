<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Udyog — National Inventory & Resource Management Portal</title>
<style>
  :root{
    --navy:#0b2340;
    --navy-deep:#081a30;
    --orange:#e88a1f;
    --orange-deep:#c96f0d;
    --cream:#f3f1ea;
    --ink:#16233a;
    --grey:#5a6472;
    --line:#dcd7c9;
    --white:#ffffff;
  }
  *{box-sizing:border-box; margin:0; padding:0;}
  body{
    font-family:'Georgia','Noto Serif',serif;
    background:var(--cream);
    color:var(--ink);
  }
  .util-strip{
    background:var(--navy-deep);
    color:#c9d3e0;
    font-family:Arial,Helvetica,sans-serif;
    font-size:12px;
    letter-spacing:.02em;
    padding:6px 40px;
    display:flex;
    justify-content:space-between;
  }
  .util-strip span{margin-right:18px;}
  header{
    background:var(--navy);
    padding:0 40px;
    display:flex;
    align-items:center;
    justify-content:space-between;
    border-bottom:3px solid var(--orange);
  }
  .brand{
    display:flex;
    align-items:center;
    gap:14px;
    padding:14px 0;
  }
  .emblem{
    width:52px;height:52px;
    background:var(--orange);
    border-radius:4px;
    display:flex;align-items:center;justify-content:center;
    font-family:Arial,sans-serif;
    font-weight:700;
    color:var(--navy);
    font-size:22px;
    flex-shrink:0;
  }
  .brand h1{
    font-family:Arial,Helvetica,sans-serif;
    font-size:22px;
    letter-spacing:.06em;
    color:var(--white);
    font-weight:700;
  }
  .brand p{
    font-family:Arial,Helvetica,sans-serif;
    font-size:11px;
    letter-spacing:.08em;
    color:var(--orange);
    text-transform:uppercase;
    margin-top:2px;
  }
  nav{
    display:flex;
    align-items:center;
    gap:32px;
    font-family:Arial,Helvetica,sans-serif;
    font-size:13px;
    letter-spacing:.04em;
  }
  nav a{
    color:#dbe2ec;
    text-decoration:none;
    text-transform:uppercase;
    padding:22px 0;
    border-bottom:2px solid transparent;
  }
  nav a.active, nav a:hover{
    color:var(--orange);
    border-bottom:2px solid var(--orange);
  }
  .btn-primary{
    background:var(--orange);
    color:var(--navy-deep);
    font-family:Arial,sans-serif;
    font-weight:700;
    font-size:12.5px;
    letter-spacing:.05em;
    padding:11px 22px;
    text-transform:uppercase;
    text-decoration:none;
    border:none;
    cursor:pointer;
  }
  .btn-primary:hover{background:var(--orange-deep);}
  .btn-outline{
    border:1.5px solid #4a5a75;
    color:#dbe2ec;
    font-family:Arial,sans-serif;
    font-weight:700;
    font-size:12.5px;
    letter-spacing:.05em;
    padding:9.5px 20px;
    text-transform:uppercase;
    text-decoration:none;
    background:transparent;
    cursor:pointer;
  }

  /* HERO */
  .hero{
    display:grid;
    grid-template-columns:1.05fr .95fr;
    background:var(--white);
    border-bottom:1px solid var(--line);
  }
  .hero-text{
    padding:64px 56px;
  }
  .file-number{
    font-family:Arial,sans-serif;
    font-size:11.5px;
    letter-spacing:.12em;
    color:var(--orange-deep);
    text-transform:uppercase;
    font-weight:700;
    border-left:3px solid var(--orange);
    padding-left:10px;
    margin-bottom:22px;
  }
  .hero-text h2{
    font-size:46px;
    line-height:1.14;
    color:var(--navy);
    font-weight:400;
    margin-bottom:22px;
  }
  .hero-text h2 em{
    font-style:normal;
    color:var(--orange-deep);
    border-bottom:2px solid var(--orange);
  }
  .hero-text p{
    font-family:Arial,Helvetica,sans-serif;
    font-size:15px;
    line-height:1.7;
    color:var(--grey);
    max-width:480px;
    margin-bottom:32px;
  }
  .hero-ctas{display:flex; gap:14px; margin-bottom:36px;}
  .reg-no{
    font-family:Arial,sans-serif;
    font-size:11.5px;
    color:#8a8578;
    letter-spacing:.03em;
  }
  .hero-panel{
    background:var(--navy);
    background-image:
      linear-gradient(160deg, var(--navy) 0%, var(--navy-deep) 100%);
    position:relative;
    padding:56px 48px;
    color:var(--white);
    display:flex;
    flex-direction:column;
    justify-content:center;
  }
  .hero-panel::before{
    content:"";
    position:absolute; inset:14px;
    border:1px solid rgba(232,138,31,.35);
    pointer-events:none;
  }
  .ledger-title{
    font-family:Arial,sans-serif;
    font-size:11px;
    letter-spacing:.14em;
    text-transform:uppercase;
    color:var(--orange);
    margin-bottom:20px;
    font-weight:700;
  }
  .ledger-row{
    display:flex;
    justify-content:space-between;
    align-items:baseline;
    padding:16px 0;
    border-bottom:1px solid rgba(255,255,255,.12);
    font-family:Arial,sans-serif;
  }
  .ledger-row:last-child{border-bottom:none;}
  .ledger-row .num{
    font-family:Georgia,serif;
    font-size:30px;
    color:var(--white);
    font-weight:700;
  }
  .ledger-row .lbl{
    font-size:11.5px;
    letter-spacing:.05em;
    text-transform:uppercase;
    color:#a9b6c9;
    text-align:right;
  }

  /* STRIP STATS */
  .stats-strip{
    display:grid;
    grid-template-columns:repeat(4,1fr);
    background:var(--navy-deep);
  }
  .stats-strip div{
    padding:22px 30px;
    border-right:1px solid rgba(255,255,255,.08);
    font-family:Arial,sans-serif;
  }
  .stats-strip div:last-child{border-right:none;}
  .stats-strip .n{color:var(--orange); font-size:13px; font-weight:700; letter-spacing:.04em;}
  .stats-strip .t{color:#9fabc0; font-size:12px; margin-top:4px;}

  /* MODULES */
  .section{
    padding:70px 56px;
    max-width:1280px;
    margin:0 auto;
  }
  .section-head{
    display:flex;
    justify-content:space-between;
    align-items:flex-end;
    border-bottom:2px solid var(--navy);
    padding-bottom:16px;
    margin-bottom:40px;
  }
  .section-head .eyebrow{
    font-family:Arial,sans-serif;
    font-size:11px;
    letter-spacing:.14em;
    text-transform:uppercase;
    color:var(--orange-deep);
    font-weight:700;
    margin-bottom:8px;
  }
  .section-head h3{
    font-size:30px;
    color:var(--navy);
    font-weight:400;
  }
  .section-head .idx{
    font-family:Arial,sans-serif;
    font-size:13px;
    color:var(--grey);
  }

  .modules-grid{
    display:grid;
    grid-template-columns:repeat(3,1fr);
    gap:1px;
    background:var(--line);
    border:1px solid var(--line);
  }
  .module-card{
    background:var(--white);
    padding:32px 28px;
    transition:background .15s;
  }
  .module-card:hover{background:#fbf8f0;}
  .module-card .mnum{
    font-family:Arial,sans-serif;
    font-size:11px;
    color:var(--orange-deep);
    font-weight:700;
    letter-spacing:.08em;
    margin-bottom:14px;
  }
  .module-card h4{
    font-size:19px;
    color:var(--navy);
    font-weight:700;
    margin-bottom:10px;
    font-family:Arial,sans-serif;
  }
  .module-card p{
    font-family:Arial,sans-serif;
    font-size:13.5px;
    line-height:1.65;
    color:var(--grey);
  }

  /* PROCESS */
  .process{
    background:var(--white);
    border-top:1px solid var(--line);
    border-bottom:1px solid var(--line);
  }
  .process-inner{
    max-width:1280px;
    margin:0 auto;
    padding:70px 56px;
    display:grid;
    grid-template-columns:repeat(4,1fr);
    gap:0;
  }
  .step{
    padding-right:28px;
    border-left:3px solid var(--orange);
    padding-left:20px;
    position:relative;
  }
  .step + .step{margin-left:0;}
  .step .sn{
    font-family:Arial,sans-serif;
    font-size:34px;
    font-weight:700;
    color:var(--navy);
    opacity:.15;
    margin-bottom:6px;
  }
  .step h5{
    font-family:Arial,sans-serif;
    font-size:15px;
    color:var(--navy);
    font-weight:700;
    margin-bottom:8px;
  }
  .step p{
    font-family:Arial,sans-serif;
    font-size:13px;
    color:var(--grey);
    line-height:1.6;
  }

  /* NOTICE */
  .notice-band{
    background:var(--orange);
    color:var(--navy-deep);
    font-family:Arial,sans-serif;
    padding:22px 56px;
    display:flex;
    justify-content:space-between;
    align-items:center;
  }
  .notice-band h4{font-size:19px; font-weight:700;}
  .notice-band p{font-size:13px; margin-top:4px; max-width:520px;}

  footer{
    background:var(--navy-deep);
    color:#9fabc0;
    font-family:Arial,sans-serif;
    padding:48px 56px 24px;
  }
  .footer-grid{
    display:grid;
    grid-template-columns:2fr 1fr 1fr 1fr;
    gap:40px;
    padding-bottom:32px;
    border-bottom:1px solid rgba(255,255,255,.1);
  }
  .footer-grid h6{
    color:var(--orange);
    font-size:12px;
    letter-spacing:.08em;
    text-transform:uppercase;
    margin-bottom:14px;
  }
  .footer-grid a{
    display:block;
    color:#9fabc0;
    text-decoration:none;
    font-size:13px;
    margin-bottom:10px;
  }
  .footer-grid p{font-size:13px; line-height:1.7; max-width:320px;}
  .foot-bottom{
    padding-top:20px;
    display:flex;
    justify-content:space-between;
    font-size:11.5px;
    letter-spacing:.03em;
  }

  @media(max-width:900px){
    .hero{grid-template-columns:1fr;}
    .modules-grid{grid-template-columns:1fr;}
    .process-inner{grid-template-columns:1fr; gap:28px;}
    nav{display:none;}
    .stats-strip{grid-template-columns:repeat(2,1fr);}
    .footer-grid{grid-template-columns:1fr 1fr;}
  }
</style>
</head>
<body>

  <div class="util-strip">
    <div><span>Skip to Main Content</span><span>Screen Reader Access</span></div>
    <div><span>हिन्दी</span><span>English</span></div>
  </div>

  <header>
    <div class="brand">
      <div class="emblem">उ</div>
      <div>
        <h1>UDYOG</h1>
        <p>Inventory &amp; Resource Management Portal</p>
      </div>
    </div>
    <nav>
      <a class="active" href="#">Home</a>
      <a href="#">About</a>
      <a href="#">Modules</a>
      <a href="#">Reports</a>
      <a href="#">Support</a>
    </nav>
    <div style="display:flex; gap:10px; padding:20px 0;">
      <a class="btn-outline" href="#">Login</a>
      <a class="btn-primary" href="#">Register Entity</a>
    </div>
  </header>

  <section class="hero">
    <div class="hero-text">
      <div class="file-number">Ref No. UDY/INV/2026 — Unified Stock Ledger</div>
      <h2>Where Stock<br>Becomes <em>Record</em>.</h2>
      <p>Udyog is a unified inventory and resource ledger for organisations that need audit-grade tracking — stock intake, issue, transfer and reconciliation, recorded the way a registry should be.</p>
      <div class="hero-ctas">
        <a class="btn-primary" href="#">Open Dashboard</a>
        <a class="btn-outline" style="color:var(--navy); border-color:var(--navy);" href="#">View Documentation</a>
      </div>
      <div class="reg-no">Empanelled for institutional, industrial and departmental use · Est. 2026</div>
    </div>
    <div class="hero-panel">
      <div class="ledger-title">Live Ledger Summary</div>
      <div class="ledger-row"><div class="num">1,248</div><div class="lbl">Stock Keeping<br>Units Tracked</div></div>
      <div class="ledger-row"><div class="num">37</div><div class="lbl">Warehouses<br>Connected</div></div>
      <div class="ledger-row"><div class="num">96.4%</div><div class="lbl">Reconciliation<br>Accuracy</div></div>
      <div class="ledger-row"><div class="num">04</div><div class="lbl">Pending<br>Approvals</div></div>
    </div>
  </section>

  <div class="stats-strip">
    <div><div class="n">12,400+</div><div class="t">Items under management</div></div>
    <div><div class="n">220</div><div class="t">Registered organisations</div></div>
    <div><div class="n">99.9%</div><div class="t">Ledger uptime</div></div>
    <div><div class="n">24×7</div><div class="t">Audit trail logging</div></div>
  </div>

  <section class="section">
    <div class="section-head">
      <div>
        <div class="eyebrow">Core Register</div>
        <h3>Modules of the Portal</h3>
      </div>
      <div class="idx">06 Modules Active</div>
    </div>
    <div class="modules-grid">
      <div class="module-card">
        <div class="mnum">MODULE 01</div>
        <h4>Stock Ledger</h4>
        <p>Maintain a running, timestamped record of every item — quantity, location, unit value and condition — with full revision history.</p>
      </div>
      <div class="module-card">
        <div class="mnum">MODULE 02</div>
        <h4>Purchase &amp; Intake</h4>
        <p>Log incoming consignments against purchase orders, verify against invoices, and route discrepancies for review.</p>
      </div>
      <div class="module-card">
        <div class="mnum">MODULE 03</div>
        <h4>Issue &amp; Transfer</h4>
        <p>Record material issued to departments or transferred between warehouses, with signatory approval at every step.</p>
      </div>
      <div class="module-card">
        <div class="mnum">MODULE 04</div>
        <h4>Audit &amp; Reconciliation</h4>
        <p>Schedule periodic stock counts, flag variances automatically, and generate reconciliation reports for sign-off.</p>
      </div>
      <div class="module-card">
        <div class="mnum">MODULE 05</div>
        <h4>Vendor Directory</h4>
        <p>Maintain empanelled supplier records, rate contracts and performance history in one searchable register.</p>
      </div>
      <div class="module-card">
        <div class="mnum">MODULE 06</div>
        <h4>Reports &amp; Returns</h4>
        <p>Generate statutory and internal reports — stock statements, valuation summaries, movement registers — on demand.</p>
      </div>
    </div>
  </section>

  <div class="process">
    <div class="process-inner">
      <div class="step">
        <div class="sn">01</div>
        <h5>Register Entity</h5>
        <p>Onboard your organisation and define warehouses, departments and approving authorities.</p>
      </div>
      <div class="step">
        <div class="sn">02</div>
        <h5>Digitise Inventory</h5>
        <p>Bulk-upload existing stock records or enter items individually into the ledger.</p>
      </div>
      <div class="step">
        <div class="sn">03</div>
        <h5>Track Movement</h5>
        <p>Every intake, issue and transfer is logged with a timestamp and responsible signatory.</p>
      </div>
      <div class="step">
        <div class="sn">04</div>
        <h5>Reconcile &amp; Report</h5>
        <p>Run scheduled audits and export reports for internal review or statutory filing.</p>
      </div>
    </div>
  </div>

  <div class="notice-band">
    <div>
      <h4>Notice: Portal Migration to Udyog 2.0</h4>
      <p>Organisations currently maintaining registers in spreadsheets are advised to migrate before the next audit cycle. Assisted onboarding available.</p>
    </div>
    <a class="btn-outline" style="color:var(--navy-deep); border-color:var(--navy-deep);" href="#">Request Migration Support</a>
  </div>

  <footer>
    <div class="footer-grid">
      <div>
        <h6>Udyog Portal</h6>
        <p>A unified inventory and resource management ledger built for institutions, industries and government-affiliated bodies that require accountable, auditable stock records.</p>
      </div>
      <div>
        <h6>Portal</h6>
        <a href="#">About Udyog</a>
        <a href="#">Modules</a>
        <a href="#">Documentation</a>
      </div>
      <div>
        <h6>Support</h6>
        <a href="#">Help Desk</a>
        <a href="#">Grievance Redressal</a>
        <a href="#">Contact Registry</a>
      </div>
      <div>
        <h6>Compliance</h6>
        <a href="#">Terms of Use</a>
        <a href="#">Privacy Policy</a>
        <a href="#">Data Retention</a>
      </div>
    </div>
    <div class="foot-bottom">
      <span>© 2026 Udyog Inventory Portal. All rights reserved.</span>
      <span>Best viewed at 1280×800 resolution</span>
    </div>
  </footer>

</body>
</html>