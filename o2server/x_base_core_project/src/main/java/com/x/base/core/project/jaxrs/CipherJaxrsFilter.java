package com.x.base.core.project.jaxrs;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionUnauthorized;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.FilterTools;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;

/**
 * 프런트에서 이미 로그인한 사용자만 접근 가능
 */
public abstract class CipherJaxrsFilter extends TokenFilter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			httpRequestCheck(request);
			FilterTools.allow(request, response);
			if (!request.getMethod().equalsIgnoreCase("options")) {
				HttpToken httpToken = new HttpToken();
				EffectivePerson effectivePerson = httpToken.who(request, response, Config.token().getCipher());
				if (TokenType.anonymous.equals(effectivePerson.getTokenType())) {
					// 401 Unauthorized 미로그인 접근 거부
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setHeader("Content-Type", "application/json;charset=UTF-8");
					ActionResult<?> result = new ActionResult<>();
					ExceptionUnauthorized e = new ExceptionUnauthorized();
					result.error(e);
					String message = e.getFormatMessage(result.getPrompt(),
							request.getHeader(ResponseFactory.Accept_Language));
					if (StringUtils.isNotBlank(message)) {
						result.setMessage(message);
					}
					response.getWriter().write(result.toJson());
				} else if (!TokenType.cipher.equals(effectivePerson.getTokenType())) {
					// 500 표시를 직접 해야 함
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.setHeader("Content-Type", "application/json;charset=UTF-8");
					ActionResult<?> result = new ActionResult<>();
					ExceptionAccessDenied e = new ExceptionAccessDenied(effectivePerson);
					result.error(e);
					String message = e.getFormatMessage(result.getPrompt(),
							request.getHeader(ResponseFactory.Accept_Language));
					if (StringUtils.isNotBlank(message)) {
						result.setMessage(message);
					}
					response.getWriter().write(result.toJson());
				} else {
					chain.doFilter(request, response);
				}
			} else {
				options(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
	}

	public void init(FilterConfig config) throws ServletException {
	}
}
