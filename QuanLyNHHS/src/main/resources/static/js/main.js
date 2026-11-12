document.addEventListener('DOMContentLoaded', function () {
  // 1. Toast Notification Helper
  window.showToast = function (message, type = 'success') {
    let container = document.querySelector('.toast-container');
    if (!container) {
      container = document.createElement('div');
      container.className = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = 'toast';
    const iconClass = type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation';
    toast.innerHTML = `<i class="fa-solid ${iconClass}"></i><span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(10px)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 3200);
  };

  // 2. Filter Pills for Dishes (Món ăn)
  const filterPills = document.querySelectorAll('.pill-btn[data-category]');
  const dishCards = document.querySelectorAll('.dish-card');
  const dishTableRows = document.querySelectorAll('#dishTableBody tr');

  if (filterPills.length > 0) {
    filterPills.forEach(pill => {
      pill.addEventListener('click', function () {
        filterPills.forEach(p => p.classList.remove('active'));
        this.classList.add('active');

        const selectedCat = this.getAttribute('data-category');

        // Filter cards
        dishCards.forEach(card => {
          const cardCat = card.getAttribute('data-category');
          if (selectedCat === 'all' || cardCat === selectedCat) {
            card.style.display = 'flex';
          } else {
            card.style.display = 'none';
          }
        });

        // Filter table rows
        dishTableRows.forEach(row => {
          const rowCat = row.getAttribute('data-category');
          if (selectedCat === 'all' || rowCat === selectedCat) {
            row.style.display = '';
          } else {
            row.style.display = 'none';
          }
        });
      });
    });
  }

  // 3. Search Bar for Dishes
  const searchInput = document.getElementById('searchDishInput');
  if (searchInput) {
    searchInput.addEventListener('input', function () {
      const keyword = this.value.toLowerCase().trim();

      dishCards.forEach(card => {
        const title = card.querySelector('.dish-title').textContent.toLowerCase();
        const code = card.querySelector('.dish-code').textContent.toLowerCase();
        if (title.includes(keyword) || code.includes(keyword)) {
          card.style.display = 'flex';
        } else {
          card.style.display = 'none';
        }
      });

      dishTableRows.forEach(row => {
        const text = row.textContent.toLowerCase();
        if (text.includes(keyword)) {
          row.style.display = '';
        } else {
          row.style.display = 'none';
        }
      });
    });
  }

  // 4. View Switch: Grid Card vs Table View
  const btnGridView = document.getElementById('btnGridView');
  const btnTableView = document.getElementById('btnTableView');
  const gridContainer = document.getElementById('dishGridContainer');
  const tableContainer = document.getElementById('dishTableContainer');

  if (btnGridView && btnTableView && gridContainer && tableContainer) {
    btnGridView.addEventListener('click', function () {
      btnGridView.classList.add('active');
      btnTableView.classList.remove('active');
      gridContainer.style.display = 'grid';
      tableContainer.style.display = 'none';
    });

    btnTableView.addEventListener('click', function () {
      btnTableView.classList.add('active');
      btnGridView.classList.remove('active');
      gridContainer.style.display = 'none';
      tableContainer.style.display = 'block';
    });
  }

  // 5. Staff (Nhân viên) Search and Department Filter
  const searchStaffInput = document.getElementById('searchStaffInput');
  const staffTableRows = document.querySelectorAll('#staffTableBody tr');
  const staffRolePills = document.querySelectorAll('.pill-btn[data-role]');

  if (searchStaffInput && staffTableRows.length > 0) {
    searchStaffInput.addEventListener('input', function () {
      const keyword = this.value.toLowerCase().trim();
      staffTableRows.forEach(row => {
        const text = row.textContent.toLowerCase();
        if (text.includes(keyword)) {
          row.style.display = '';
        } else {
          row.style.display = 'none';
        }
      });
    });
  }

  if (staffRolePills.length > 0) {
    staffRolePills.forEach(pill => {
      pill.addEventListener('click', function () {
        staffRolePills.forEach(p => p.classList.remove('active'));
        this.classList.add('active');

        const role = this.getAttribute('data-role');
        staffTableRows.forEach(row => {
          const rowRole = row.getAttribute('data-role');
          if (role === 'all' || rowRole === role) {
            row.style.display = '';
          } else {
            row.style.display = 'none';
          }
        });
      });
    });
  }

  // 6. Generic Modal Open/Close Controls
  window.openModal = function (modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.classList.add('active');
    }
  };

  window.closeModal = function (modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.classList.remove('active');
    }
  };

  // Close modal when clicking outside
  document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', function (e) {
      if (e.target === this) {
        this.classList.remove('active');
      }
    });
  });

  // 7. Form Handlers Simulation
  const formAddDish = document.getElementById('formAddDish');
  if (formAddDish) {
    formAddDish.addEventListener('submit', function (e) {
      e.preventDefault();
      closeModal('modalAddDish');
      showToast('Đã thêm món ăn mới vào thực đơn thành công!', 'success');
      formAddDish.reset();
    });
  }

  const formAddStaff = document.getElementById('formAddStaff');
  if (formAddStaff) {
    formAddStaff.addEventListener('submit', function (e) {
      e.preventDefault();
      closeModal('modalAddStaff');
      showToast('Đã thêm hồ sơ nhân viên mới thành công!', 'success');
      formAddStaff.reset();
    });
  }

  const formAddCategory = document.getElementById('formAddCategory');
  if (formAddCategory) {
    formAddCategory.addEventListener('submit', function (e) {
      e.preventDefault();
      closeModal('modalAddCategory');
      showToast('Đã tạo loại món mới thành công!', 'success');
      formAddCategory.reset();
    });
  }

  // 8. Delete Confirmation Simulation
  window.confirmDelete = function (itemName) {
    if (confirm(`Bạn có chắc chắn muốn xóa "${itemName}" không? Hành động này không thể hoàn tác.`)) {
      showToast(`Đã xóa thành công "${itemName}"!`, 'success');
    }
  };
});
