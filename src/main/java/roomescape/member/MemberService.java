package roomescape.member;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;
import roomescape.provider.TokenProvider;

@Service
public class MemberService {
    private MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = memberRepository.save(new Member(memberRequest.getName(), memberRequest.getEmail(), memberRequest.getPassword(), "USER"));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public Member findMemberByEmailAndPassword(String email, String password) {
        Member member = memberRepository.findByEmailAndPassword(email, password);
        return member;
    }
    public String createToken(Member member) {
        String accessToken = TokenProvider.createToken(member);
        return accessToken;
    }
    public String extractTokenFromCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                return cookie.getValue();
            }
        }

        return "";
    }

    public Member findByToken(String token) {
        Long memberId = Long.valueOf(Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=".getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody().getSubject());
        Member member = memberRepository.findById(memberId).orElseThrow();
        return member;
    }
}
