"use strict";
(() => {
  const sidebar = document.querySelector(".sidebar");
  const toggle = document.querySelector(".menu-toggle");
  if (!sidebar || !toggle) return;
  const backdrop = document.createElement("button");
  backdrop.className = "sidebar-backdrop";
  backdrop.type = "button";
  backdrop.setAttribute("aria-label", "Đóng menu");
  backdrop.tabIndex = -1;
  document.body.appendChild(backdrop);
  const setOpen = (open) => {
    sidebar.classList.toggle("mobile-open", open);
    backdrop.classList.toggle("is-visible", open);
    document.body.classList.toggle("sidebar-is-open", open);
    toggle.setAttribute("aria-expanded", String(open));
    toggle.setAttribute("aria-label", open ? "Đóng menu" : "Mở menu");
  };
  toggle.addEventListener("click", () => setOpen(!sidebar.classList.contains("mobile-open")));
  backdrop.addEventListener("click", () => setOpen(false));
  document.addEventListener("keydown", (event) => { if (event.key === "Escape") setOpen(false); });
  sidebar.querySelectorAll("a").forEach((link) => link.addEventListener("click", () => setOpen(false)));
  window.matchMedia("(min-width: 993px)").addEventListener("change", (event) => { if (event.matches) setOpen(false); });
})();
