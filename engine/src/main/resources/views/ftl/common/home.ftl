<#-- @ftlvariable name="" type="uk.co.froot.demo.openid.views.PublicFreemarkerView" -->
<!DOCTYPE html>
<html lang="en">
<head>
<#include "../includes/common/head.ftl">
</head>

<body>
<div>
<#include "../includes/common/header.ftl">

  <h1>SICSSense Engine</h1>

  <p>Login using <a href="/openid/login">OpenID</a> or <a href="/password/login">Username/Password</a> to allow access you personal data streams.</p>

  <p><a href="/private/home">Access protected info</a>. This is available to anyone after authentication</p>

  <p><a href="/private/admin">Access private info</a>. 
	This is only available to people who authenticate with the
  specific email address set in <code>PublicOpenIDResource</code>.</p>

  <#include "../includes/common/footer.ftl">

</div>

<#include "../includes/common/cdn-scripts.ftl">

</body>
</html>
