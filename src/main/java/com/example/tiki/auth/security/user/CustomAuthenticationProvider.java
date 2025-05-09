package com.example.tiki.auth.security.user;//package com.guideme.guideme.security.provider;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class CustomAuthenticationProvider implements AuthenticationProvider {
//
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String username = authentication.getName();
//        String verification = authentication.getCredentials().toString();
//
//        UserDetails user = userDetailsService.loadUserByUsername(username);
//
//        if(user.getPassword() == null){
//            throw new BadCredentialsException("인증번호를 먼저 발급받아주세요.");
//        }
//        // 평문 비교
//        else if (!user.getPassword().equals(verification)) {
//            throw new BadCredentialsException("인증번호가 틀렸습니다.");
//        }
//
//        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
////        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
//        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
//    }
//}
