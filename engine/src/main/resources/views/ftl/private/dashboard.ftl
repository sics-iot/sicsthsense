<#-- @ftlvariable name="" type="se.sics.sicsthsense.views.PublicFreemarkerView" -->
<!DOCTYPE html>
<html lang="en">
<head>
<#include "../includes/common/head.ftl">
</head>

<body>
<div>
<#include "../includes/common/header.ftl">

  <h1>Your personal dashboard</h1>

	Hello ${user.username!"Stranger"}.

	Your <a href="/users/${user.id}">profile</a> <br \>
	

  <p>Try to get to the <a href="/private/admin">admin page</a></p>

Resources:
<#list user.getResources() as resource>
  ${resource.label} <br />
</#list>

<#include "../includes/common/footer.ftl">

</div>

<#include "../includes/common/cdn-scripts.ftl">

</body>
</html>
