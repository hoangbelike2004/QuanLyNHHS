'use strict';
document.querySelectorAll('form[data-confirm]').forEach(form => form.addEventListener('submit', event => {
  if (!window.confirm(form.dataset.confirm)) event.preventDefault();
}));
const table = document.querySelector('#receipt-lines tbody');
if (table) {
  const template = table.querySelector('[data-line]')?.cloneNode(true);
  const reindex = () => table.querySelectorAll('[data-line]').forEach((row, index) => {
    row.querySelectorAll('input, select').forEach(field => {
      field.name = field.name.replace(/chiTiet\[\d+\]/, 'chiTiet[' + index + ']');
      field.removeAttribute('id');
    });
  });
  document.getElementById('add-line').addEventListener('click', () => {
    if (!template || table.children.length >= 100) return;
    const row = template.cloneNode(true);
    row.querySelectorAll('input, select').forEach(field => { field.value = ''; field.removeAttribute('id'); });
    table.append(row); reindex(); row.querySelector('select').focus();
  });
  table.addEventListener('click', event => {
    if (event.target.closest('[data-remove]') && table.children.length > 1) {
      event.target.closest('[data-line]').remove(); reindex();
    }
  });
}
