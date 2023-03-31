<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="constants.ForwardConst" %>
<%@ page import="constants.AttributeConst" %>

<c:set var="actRep" value="${ForwardConst.ACT_REP.getValue()}" />
<c:set var="commIdx" value="${ForwardConst.CMD_INDEX.getValue()}" />
<c:set var="commShow" value="${ForwardConst.CMD_SHOW.getValue()}" />
<c:set var="commNew" value="${ForwardConst.CMD_NEW.getValue()}" />
<c:set var="commTimeline" value="${ForwardConst.CMD_TIMELINE.getValue()}" />

<c:import url="/WEB-INF/views/layout/app.jsp">
    <c:param name="content">
    <c:if test="${flush != null}">
        <div id ="flush_success">
            <c:out value="${flush}" />
        </div>
    </c:if>
    <h2>フォローしている従業員のタイムライン</h2>
    <table id="talble_list">
        <tbody>
            <tr>
                <th class="report_name">氏名</th>
                <th class="report_date">日付</th>
                <th class="report_title">タイトル</th>
                <th class="report_action">操作</th>
                <th class="report_status">承認状況</th>
            </tr>

            <c:forEach var="report" items="${reports}" varStatus="status">
                <fmt:parseDate value="${report.reportDate}" pattern="yyyy-MM-dd" var="reportDay" type="date" />
                <tr>
                    <td class="report_name"><c:out value="${report.employee.name}" /></td>
                    <td class="report_date"><fmt:formatDate value="${reportDay}" pattern='yyyy-MM-dd' /></td>
                    <td class="report_title"><c:out value="${report.title}" /></td>
                    <td class="report_action"><a href="<c:url value='?action=${actRep}&command=${commShow}&id=${report.id}' />">詳細</a></td>
                    <td class="report_status">
                        <c:choose>
                            <c:when test="${report.approveStatus == AttributeConst.REP_APPROVE_STATUS_UNAPPROVED.getIntegerValue()}">未承認</c:when>
                            <c:when test="${report.approveStatus == AttributeConst.REP_APPROVE_STATUS_1ST_APPROVED.getIntegerValue()}">一次承認済</c:when>
                            <c:otherwise>最終承認済</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div id="pagenation">
        （全 ${reports_count} 件）<br />
        <c:forEach var="i" begin="1" end="${((reports_count - 1) / maxRow) + 1}" step="1">
            <c:choose>
                <c:when test="${i == page}">
                    <c:out value="${i}" />&nbsp;
                </c:when>
                <c:otherwise>
                    <a href="<c:url value='?action=${actRep}&command=${commTimeline}&page=${i}' />"><c:out value="${i}" /></a>&nbsp;
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </div>
    <p><a href="<c:url value='?action=${actRep}&command=${commIdx}' />">日報一覧に戻る</a></p>
    </c:param>
</c:import>
