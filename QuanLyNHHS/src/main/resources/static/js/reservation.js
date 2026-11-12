"use strict";

document.querySelectorAll(".reservation-module .needs-validation").forEach((form) => {
  form.addEventListener("submit", (event) => {
    if (!form.checkValidity()) {
      event.preventDefault();
      event.stopPropagation();
    }
    form.classList.add("was-validated");
  });
});

document.querySelectorAll(".reservation-module [data-bs-dismiss='alert']").forEach((button) => {
  button.setAttribute("aria-label", "Đóng thông báo");
  button.textContent = "×";
  button.addEventListener("click", () => button.closest(".alert")?.remove());
});
