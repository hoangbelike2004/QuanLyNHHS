const { test } = require('node:test');
const assert = require('node:assert/strict');
const { readFileSync } = require('node:fs');
const { resolve } = require('node:path');
const { runInNewContext } = require('node:vm');

function element(text = '', group = '') {
  const listeners = {};
  const classes = new Set();
  return {
    textContent: text, value: '', style: {},
    getAttribute: () => group,
    addEventListener: (event, handler) => { listeners[event] = handler; },
    fire(event) { listeners[event]?.call(this); },
    classList: {
      add: name => classes.add(name), remove: name => classes.delete(name),
      toggle(name, enabled) { enabled ? classes.add(name) : classes.delete(name); },
      contains: name => classes.has(name)
    }
  };
}

function setup() {
  const inputs = Object.fromEntries(['searchDishInput', 'searchStaffInput', 'searchCategoryInput',
    'btnGridView', 'btnTableView', 'dishGridContainer', 'dishTableContainer'].map(id => [id, element()]));
  const dishes = [element('Tôm hấp', '1'), element('Cá hấp', '2'), element('Tôm nướng', '2')];
  const staff = [element('An Thu ngân', 'Thu ngân'), element('Bình Bếp', 'Bếp'), element('An Bếp', 'Bếp')];
  const categories = [element('01 Tôm'), element('02 Cá')];
  const dishPills = ['all', '1', '2'].map(value => element('', value));
  const staffPills = ['all', 'Thu ngân', 'Bếp'].map(value => element('', value));
  const selectors = {
    '.pill-btn[data-category]': dishPills,
    '.dish-card, #dishTableBody tr[data-category]': dishes,
    '.pill-btn[data-role]': staffPills,
    '#staffTableBody tr[data-role]': staff,
    '#categoryTableBody tr[data-category-row]': categories
  };
  const document = {
    getElementById: id => inputs[id] || null,
    querySelectorAll: selector => selectors[selector] || [],
    querySelector: () => null,
    addEventListener: (event, handler) => { if (event === 'DOMContentLoaded') handler(); }
  };
  const script = readFileSync(resolve(__dirname, '../../main/resources/static/js/main.js'), 'utf8');
  runInNewContext(script, { document });
  return { inputs, dishes, staff, categories, dishPills, staffPills };
}

const visible = rows => rows.filter(row => row.style.display !== 'none').map(row => row.textContent);

test('dish search and category filter remain combined when either changes', () => {
  const { inputs, dishes, dishPills } = setup();
  inputs.searchDishInput.value = 'tôm';
  inputs.searchDishInput.fire('input');
  assert.deepEqual(visible(dishes), ['Tôm hấp', 'Tôm nướng']);
  dishPills[2].fire('click');
  assert.deepEqual(visible(dishes), ['Tôm nướng']);
  inputs.searchDishInput.value = '';
  inputs.searchDishInput.fire('input');
  assert.deepEqual(visible(dishes), ['Cá hấp', 'Tôm nướng']);
  inputs.searchDishInput.value = 'cá';
  inputs.searchDishInput.fire('input');
  dishPills[0].fire('click');
  assert.deepEqual(visible(dishes), ['Cá hấp']);
});

test('employee search preserves selected position and vice versa', () => {
  const { inputs, staff, staffPills } = setup();
  staffPills[2].fire('click');
  inputs.searchStaffInput.value = 'an';
  inputs.searchStaffInput.fire('input');
  assert.deepEqual(visible(staff), ['An Bếp']);
  staffPills[0].fire('click');
  assert.deepEqual(visible(staff), ['An Thu ngân', 'An Bếp']);
});

test('category search handles names, codes, and clearing the query', () => {
  const { inputs, categories } = setup();
  inputs.searchCategoryInput.value = '02';
  inputs.searchCategoryInput.fire('input');
  assert.deepEqual(visible(categories), ['02 Cá']);
  inputs.searchCategoryInput.value = '';
  inputs.searchCategoryInput.fire('input');
  assert.equal(visible(categories).length, 2);
});

test('dish view buttons switch containers and active indicators', () => {
  const { inputs } = setup();
  inputs.btnGridView.fire('click');
  assert.equal(inputs.dishGridContainer.style.display, 'grid');
  assert.equal(inputs.dishTableContainer.style.display, 'none');
  assert.equal(inputs.btnGridView.classList.contains('active'), true);
  inputs.btnTableView.fire('click');
  assert.equal(inputs.dishGridContainer.style.display, 'none');
  assert.equal(inputs.dishTableContainer.style.display, 'block');
  assert.equal(inputs.btnGridView.classList.contains('active'), false);
});
