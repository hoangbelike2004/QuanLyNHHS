"use strict";
document.querySelector("[data-show-password]")?.addEventListener("change", (event) => {
  document.querySelectorAll(".auth-password").forEach((input) => {
    input.type = event.target.checked ? "text" : "password";
  });
});
