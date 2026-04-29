(() => {
  const menuBtn = document.querySelector('[data-menu-toggle]');
  const menu = document.querySelector('[data-menu]');

  if (menuBtn && menu) {
    menuBtn.addEventListener('click', () => {
      menu.classList.toggle('open');
    });
  }

  const filters = document.querySelectorAll('[data-filter-input]');
  filters.forEach((input) => {
    const target = document.querySelector(input.getAttribute('data-filter-input'));
    if (!target) return;

    input.addEventListener('input', () => {
      const value = input.value.trim().toLowerCase();
      const rows = target.querySelectorAll('tbody tr');
      rows.forEach((row) => {
        const visible = row.textContent.toLowerCase().includes(value);
        row.style.display = visible ? '' : 'none';
      });
    });
  });
})();
