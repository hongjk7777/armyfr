function sendEmail(){
  var email = document.getElementById("username").value;
//  const mail = $('#username').text();
  const url = "/user/login/sendMail?email=";
  const emailSend = url + email;

  location.replace(emailSend);


}