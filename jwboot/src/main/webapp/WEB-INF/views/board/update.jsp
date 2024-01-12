<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>글 수정하기</title>
<link rel="stylesheet" href="/static/css/style.css">
</head>
<body>
	<div class="wrap">
		<h2>글 수정하기</h2>
		<form action="/board/update" method="post">
		<input type="hidden" name="id" value="${board.id}">
			<table class="tbl_write">
				<tbody>
					<tr>
						<td><input type="text" name="title" value="${board.title}"></td>
					</tr>
					<tr>
						<td><input type="text" name="writer" value="${board.writer}" readonly></td>
					</tr>
					<tr>
						<td><textarea rows="5" cols="60" name="content">${board.content}</textarea></td>
					</tr>
					<tr>
						<td>
							<input type="submit" value="저장">
							<a href="/board/"><button type="button">목록</button>	</a>					
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</body>
</html>