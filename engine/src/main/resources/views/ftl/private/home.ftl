<#-- @ftlvariable name="" type="se.sics.sicsthsense.PublicFreemarkerView" -->
<!DOCTYPE html>
<html lang="en">
<head>
<#include "../includes/common/head.ftl">
</head>

<body>
<div>
<#include "../includes/common/header.ftl">

  <h1>Public data</h1>

Something that has been made public by the user...

  <p><a href="/">Home</a>. 

  <p><a href="/private/dashboard">Access personal info</a>. This is available to anyone after authentication</p>

<#include "../includes/common/footer.ftl">

</div>

<#include "../includes/common/cdn-scripts.ftl">

</body>
</html>
