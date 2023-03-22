<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="constants.AttributeConst"%>
<%@ page import="constants.ForwardConst"%>

<c:set var="action" value="${ForwardConst.ACT_EMP.getValue()}" />
<c:set var="commIdx" value="${ForwardConst.CMD_INDEX.getValue()}" />
<c:set var="commUpd" value="${ForwardConst.CMD_UPDATE.getValue()}" />
<c:set var="commDel" value="${FrowardConst.CMD_DESTROY.getValue()}" />

<c:import url="../layout/app.jsp">
    <c:param name="content">

    <h2><c:out value="${employee.id} の従業員情報　編集ページ" /></h2>
    <p>（パスワードは変更する場合のみ入力してください）</p>
    <form method="POST" action="<c:url value='?action=${action}&command=${commUpd}' />">
        <c:import url="_form.jsp" />
    </form>

    <p>
        <a href="#" onclick="confirmDestory();">この従業員情報を削除する</a>
    </p>

    <form method="POST" action="<c:out value='?action=${action}&command=${commDel}' />">
        <input type="hidden" name="${AttributeConst.EMP_ID.getValue()}" value="${employee.id}" />
        <input type="hidden" name="${AttributeConst.TOKEN.getValue()}" value="${_token}" />
    </form>

    <script>
        function confirmDestory(){
            if (confirm("本当に削除してよろしいですか？")){
                document.forms[1].submit();
            }
        }
    </script>

    <p><a href="<c:out value='?action=${action}&command=${commIdx}' />">一覧に戻る</a></p>
    </c:param>
</c:import>
