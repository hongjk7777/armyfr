package project1.armyfr.handler;

import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import project1.armyfr.dto.MemberDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EchoHandler extends TextWebSocketHandler {
    //전체
    List<WebSocketSession> sessions = new ArrayList<WebSocketSession>();

    //1대1
    Map<String, WebSocketSession> userSessionsMap = new HashMap<String, WebSocketSession>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);

        String senderEmail = getEmail(session);
        userSessionsMap.put(senderEmail, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload();
        if(!StringUtils.isEmpty(msg)) {
            String[] strs = msg.split(",");

            if(strs != null && strs.length == 5) {
                String cmd = strs[0];
                String caller = strs[1];
                String receiver = strs[2];
                String receiverEmail = strs[3];
                String seq = strs[4];

                //작성자가 로그인 해서 있다면
                WebSocketSession boardWriterSession = userSessionsMap.get(receiverEmail);

                if("reply".equals(cmd) && boardWriterSession != null) {
                    TextMessage tmpMsg = new TextMessage(caller + "님이 " +
                            "<a type='external' href='/mentor/menteeboard/menteeboardView?seq="+seq+"&pg=1'>" + seq + "</a> 번 게시글에 댓글을 남겼습니다.");
                    boardWriterSession.sendMessage(tmpMsg);

                }else if("follow".equals(cmd) && boardWriterSession != null) {
                    TextMessage tmpMsg = new TextMessage(caller + "님이 " + receiver +
                            "님을 팔로우를 시작했습니다.");
                    boardWriterSession.sendMessage(tmpMsg);

                }else if("scrap".equals(cmd) && boardWriterSession != null) {
                    TextMessage tmpMsg = new TextMessage(caller + "님이 " +
                            //변수를 하나더 보낼수 없어서 receiver 변수에 member_seq를 넣어서 썼다.
                            "<a type='external' href='/mentor/essayboard/essayboardView?pg=1&seq="+seq+"&mentors="+ receiver +"'>" + seq + "</a>번 에세이를 스크랩 했습니다.");
                    boardWriterSession.sendMessage(tmpMsg);
                }
            }
            // 모임 신청 했을때
            if(strs != null && strs.length == 5) {
                String cmd = strs[0];
                String mentee_name = strs[1];
                String mentor_email = strs[2];
                String meetingboard_seq = strs[3];
                String participation_seq = strs[4];

                // 모임 작성한 멘토가 로그인 해있으면
                WebSocketSession mentorSession = userSessionsMap.get(mentor_email);
                if(cmd.equals("apply") && mentorSession != null) {
                    TextMessage tmpMsg = new TextMessage(
                            mentee_name + "님이 모임을 신청했습니다. " +"<a type='external' href='/mentor/participation/participationView?mseq="+ meetingboard_seq +"&pseq="+ participation_seq +"'>신청서 보기</a>");
                    mentorSession.sendMessage(tmpMsg);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //System.out.println("afterConnectionClosed " + session + ", " + status);
        userSessionsMap.remove(session.getId());
        sessions.remove(session);
    }

    private String getEmail(WebSocketSession session) {
        Map<String, Object> httpSession = session.getAttributes();
        MemberDto longinUser = (MemberDto) httpSession.get("memDto");

        if (longinUser == null) {
            return session.getId();
        } else {
            return longinUser.getEmail();
        }
    }
}
