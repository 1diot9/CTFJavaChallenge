<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="login.aspx.cs" Inherits="AuthPages.LoginPage" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Admin Login</title>
</head>
<body>
    <form id="form1" runat="server" method="post">
        <asp:TextBox ID="PasswordBox" runat="server" TextMode="Password" />
        <asp:Button ID="LoginBtn" runat="server" Text="Login" OnClick="LoginBtn_Click" />
        <asp:Literal ID="Message" runat="server" />
    </form>
</body>
</html>
