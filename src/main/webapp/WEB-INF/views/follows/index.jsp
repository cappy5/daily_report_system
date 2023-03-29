<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="constants.ForwardConst"%>

<c:set var="actFol" value="${ForwardConst.ACT_FOL.getValue()}" />
<c:set var="commIdx" value="${ForwardConst.CMD_INDEX.getValue()}" />
<c:set var="commDest" value="${ForwardConst.CMD_DESTROY.getValue()}" />

<c:import url="/WEB-INF/views/layout/app.jsp">
    <c:param name="content">
    <c:if test="${flush != null}">
        <div id ="flush_success">
            <c:out value="${flush}" />
        </div>
    </c:if>
    <h2>フォローしている従業員一覧</h2>
    <table id="followed_employee_list">
        <tbody>
            <tr>
                <th>社員番号</th>
                <th>氏名</th>
                <th>操作</th>
            </tr>

            <c:forEach var="employee" items="${employees}" varStatus="status">
                <tr>
                    <td><c:out value="${employee.code}" /></td>
                    <td><c:out value="${employee.name}" /></td>
                    <td><a href="<c:url value='?action=${actFol}&command=${commDest}&id=${employee.id}' />">アンフォローする</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div id="pagenation">
        （全 ${employees_count} 件）<br />
        <c:forEach var="i" begin="1" end="${((employees_count - 1) / maxRow) + 1}" step="1">
            <c:choose>
                <c:when test="${i == page}">
                    <c:out value="${i}" />&nbsp;
                </c:when>
                <c:otherwise>
                    <a href="<c:url value='?action=${actFol}&command=${commIdx}&page=${i}' />"><c:out value="${i}" /></a>&nbsp;
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </div>

    </c:param>
</c:import>
