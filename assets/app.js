function initHighlight() {
  if (typeof hljs === 'undefined') return;
  // language aliases used in the docs
  const aliases = { conf: 'apacheconf', properties: 'ini', log: 'plaintext', shell: 'bash', sh: 'bash' };
  document.querySelectorAll('pre code').forEach((block) => {
    let lang = (block.className.match(/language-([\w+-]+)/) || [])[1] || '';
    lang = aliases[lang] || lang;
    try {
      if (lang && hljs.getLanguage(lang)) {
        hljs.highlightElement(block, { language: lang, ignoreIllegals: true });
      } else {
        hljs.highlightElement(block, { ignoreIllegals: true });
      }
    } catch (e) { /* leave code as-is on any error */ }
  });
}

function initThemeToggle() {
  const btn = document.getElementById('themeToggle');
  if (!btn) return;
  const update = () => {
    const dark = document.documentElement.getAttribute('data-theme') === 'dark';
    btn.textContent = dark ? '☀️' : '🌙';
    btn.title = dark ? '切换到白天模式' : '切换到夜晚模式';
    btn.setAttribute('aria-label', btn.title);
  };
  update();
  btn.addEventListener('click', () => {
    const next = document.documentElement.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', next);
    try { localStorage.setItem('site-theme', next); } catch (e) {}
    update();
  });
}

function appInit() {
  initThemeToggle();
  initHighlight();
  const data = window.DOC_DATA || {docs:[],downloads:[]};
  const sidebar = document.getElementById('sidebar');
  if (sidebar) {
    const groups = {};
    data.docs.forEach(d => { const c = d.category_label || d.category; (groups[c] = groups[c] || []).push(d); });
    const order = window.CATEGORY_ORDER || [];
    let html = '<h2><a href="' + (window.BASE || '') + 'index.html">📚 目录</a></h2>';
    html += '<input class="search-mini" id="sideSearch" placeholder="筛选目录…">';
    Object.keys(groups).sort((a,b) => {
      const ia = order.indexOf(a), ib = order.indexOf(b);
      return (ia === -1 ? 999 : ia) - (ib === -1 ? 999 : ib) || a.localeCompare(b);
    }).forEach(cat => {
      const docs = groups[cat];
      html += '<div class="cat open" data-cat="' + cat + '"><div class="cat-title">' + cat + ' (' + docs.length + ')</div><ul>';
      docs.forEach(d => {
        const href = (window.BASE || '') + (d.href || d.out_rel);
        const active = (window.CURRENT_ID === d.id) ? ' class="active"' : '';
        html += '<li><a' + active + ' href="' + href + '">' + d.title + '</a></li>';
      });
      html += '</ul></div>';
    });
    sidebar.innerHTML = html;
    document.querySelectorAll('.cat-title').forEach(t => t.addEventListener('click', () => t.parentElement.classList.toggle('open')));
    const ss = document.getElementById('sideSearch');
    if (ss) ss.addEventListener('input', () => {
      const q = ss.value.trim().toLowerCase();
      document.querySelectorAll('.cat li a').forEach(a => {
        a.style.display = (!q || a.textContent.toLowerCase().includes(q) || a.closest('.cat').dataset.cat.toLowerCase().includes(q)) ? '' : 'none';
      });
    });
    // Keep the currently open document visible in the sidebar: scroll the
    // sidebar to its highlighted link so the reader can see where they are.
    const activeLink = sidebar.querySelector('.cat li a.active');
    if (activeLink) {
      const cat = activeLink.closest('.cat');
      if (cat && !cat.classList.contains('open')) cat.classList.add('open');
      // Always look the elements up fresh: page-translator extensions can
      // replace DOM nodes after load, detaching earlier references.
      const findLink = () => {
        const sb = document.getElementById('sidebar');
        if (!sb) return null;
        return sb.querySelector('.cat li a.active');
      };
      const inView = (link, sb) => {
        const lr = link.getBoundingClientRect();
        const sr = sb.getBoundingClientRect();
        return lr.top >= sr.top && lr.bottom <= sr.bottom;
      };
      const scrollToActive = () => {
        const sb = document.getElementById('sidebar');
        const link = findLink();
        if (!sb || !link || !link.getClientRects().length) return false;
        // scrollIntoView centers the item inside the nearest scrollable
        // ancestor (the sidebar) using the current layout each time.
        link.scrollIntoView({ block: 'center', inline: 'nearest', behavior: 'instant' });
        return inView(link, sb);
      };
      let attempts = 0;
      const tryScroll = () => {
        attempts += 1;
        const ok = scrollToActive();
        if (!ok && attempts < 30) setTimeout(tryScroll, 200);
      };
      const start = () => {
        tryScroll();
        // Re-run if a translator reflows the list later on.
        const sb = document.getElementById('sidebar');
        if (sb && window.MutationObserver) {
          let quiet;
          const mo = new MutationObserver(() => {
            clearTimeout(quiet);
            quiet = setTimeout(() => { if (attempts >= 30) scrollToActive(); }, 300);
          });
          mo.observe(sb, { childList: true, subtree: true, characterData: true });
          setTimeout(() => mo.disconnect(), 8000);
        }
      };
      if (document.readyState === 'complete') start();
      else window.addEventListener('load', start, { once: true });
    }
  }
  const toggle = document.getElementById('menuToggle');
  if (toggle) toggle.addEventListener('click', () => sidebar && sidebar.classList.toggle('show'));
  if (window.CURRENT_ID) {
    // doc page: nothing else needed
    return;
  }
  const cards = document.getElementById('cards');
  if (cards) {
    const groups = {};
    const order = window.CATEGORY_ORDER || [];
    data.docs.forEach(d => { const c = d.category_label || d.category; (groups[c] = groups[c] || []).push(d); });
    Object.keys(groups).sort((a,b) => {
      const ia = order.indexOf(a), ib = order.indexOf(b);
      return (ia === -1 ? 999 : ia) - (ib === -1 ? 999 : ib) || a.localeCompare(b);
    }).forEach(cat => {
      const docs = groups[cat];
      let h = '<div class="card"><h3>' + cat + ' <small>(' + docs.length + ')</small></h3>';
      docs.slice(0,5).forEach(d => { h += '<a href="' + (d.href || d.out_rel) + '">' + d.title + '</a>'; });
      if (docs.length > 5) h += '<a href="#"><em>还有 ' + (docs.length-5) + ' 篇，见左侧目录 →</em></a>';
      h += '</div>';
      cards.innerHTML += h;
    });
  }
  const dl = document.getElementById('downloads');
  if (dl) {
    data.downloads.forEach(d => {
      dl.innerHTML += '<div class="dl"><a href="' + d.rel + '" target="_blank">' + d.name + '</a><small>' + d.size + ' MB</small></div>';
    });
  }
  const search = document.getElementById('search');
  const results = document.getElementById('results');
  if (search && results) {
    search.addEventListener('input', () => {
      const q = search.value.trim().toLowerCase();
      if (!q) { results.innerHTML = ''; return; }
      const hits = data.docs.filter(d => d.title.toLowerCase().includes(q) || (d.category_label || d.category).toLowerCase().includes(q) || (d.id || '').toLowerCase().includes(q)).slice(0, 50);
      results.innerHTML = hits.map(d => '<a class="hit" href="' + (d.href || d.out_rel) + '">' + d.title + '<small>' + (d.category_label || d.category) + ' / ' + (d.id || '') + '</small></a>').join('') || '<p>没有匹配的文档</p>';
    });
  }
}
