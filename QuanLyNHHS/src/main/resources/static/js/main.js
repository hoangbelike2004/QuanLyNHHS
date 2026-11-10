document.addEventListener('DOMContentLoaded', function () {
  // Tìm kiếm và bộ lọc dùng chung một trạng thái để không ghi đè kết quả của nhau.
  function bindFilters(inputId, pillSelector, rowSelector, attribute, display) {
    const input = document.getElementById(inputId);
    const pills = pillSelector ? document.querySelectorAll(pillSelector) : [];
    const rows = document.querySelectorAll(rowSelector);
    let selected = 'all';
    function apply() {
      const keyword = (input?.value || '').toLocaleLowerCase('vi').trim();
      rows.forEach(row => {
        const matchesText = row.textContent.toLocaleLowerCase('vi').includes(keyword);
        const matchesGroup = selected === 'all' || row.getAttribute(attribute) === selected;
        row.style.display = matchesText && matchesGroup ? display : 'none';
      });
    }
    input?.addEventListener('input', apply);
    pills.forEach(pill => {
      pill.addEventListener('click', function () {
        pills.forEach(button => button.classList.remove('active'));
        pill.classList.add('active');
        selected = pill.getAttribute(attribute);
        apply();
      });
    });
  }

  bindFilters('searchDishInput', '.pill-btn[data-category]',
    '.dish-card, #dishTableBody tr[data-category]', 'data-category', '');
  bindFilters('searchStaffInput', '.pill-btn[data-role]',
    '#staffTableBody tr[data-role]', 'data-role', '');
  bindFilters('searchCategoryInput', null,
    '#categoryTableBody tr[data-category-row]', 'data-category-row', '');

  const gridButton = document.getElementById('btnGridView');
  const tableButton = document.getElementById('btnTableView');
  const grid = document.getElementById('dishGridContainer');
  const table = document.getElementById('dishTableContainer');
  if (gridButton && tableButton && grid && table) {
    function switchView(showGrid) {
      gridButton.classList.toggle('active', showGrid);
      tableButton.classList.toggle('active', !showGrid);
      grid.style.display = showGrid ? 'grid' : 'none';
      table.style.display = showGrid ? 'none' : 'block';
    }
    gridButton.addEventListener('click', () => switchView(true));
    tableButton.addEventListener('click', () => switchView(false));
  }

  // Biểu mẫu được mở bởi URL để nút quay lại và lỗi kiểm tra dữ liệu vẫn hoạt động.
  const dialog = document.querySelector('[role="dialog"]');
  if (dialog) {
    const firstField = dialog.querySelector('[aria-invalid="true"]')
      || dialog.querySelector('input:not([type="hidden"]), select, textarea');
    firstField?.focus();
    document.addEventListener('keydown', event => {
      if (event.key === 'Escape') dialog.querySelector('a.modal-close')?.click();
    });
  }
});
