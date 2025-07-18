function toggleLoginPassword() {
  const passwordInput = document.getElementById("login-password");
  passwordInput.type = passwordInput.type === "password" ? "text" : "password";
}
