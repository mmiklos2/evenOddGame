$('#loginForm').on('submit',hashIt);
function hashIt() {
    var pass = $('#password').textContent;
    var out = sjcl.hash.sha256.hash(pass);
    pass.textContent = sjcl.codec.hex.fromBits(out);
    return true;