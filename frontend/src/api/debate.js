import axios from "axios";

// 백 - 프론트 통신을 위한 HTTP 헤더 설정
axios.defaults.headers.post["Content-Type"] = 'application/json';
const APPLICATION_SERVER_URL = 'http://localhost:5000/';


// 세션 생성 => Back에 세션 생성 요청
async function createSession(category) {
  const response = await axios.post(
    APPLICATION_SERVER_URL + 'api/sessions',
    {
      customSessionId: category,
      userNo: 53,
      endHour: 1,
      endMinute: 30,
      quota: 16,
      isPrivacy: true
    },
    {
      headers: {
        'Content-Type': 'application/json',
      },
    }
  );

  // sessionId 반환(category)
  return response.data;
}

// 토큰 생성 => Back에 요청
async function createToken(sessionId) {
  const response = await axios.post(
    APPLICATION_SERVER_URL + 'api/sessions/' + sessionId + '/connections',
    {},
    {
      headers: {
        'Content-Type': 'application/json',
      },
    });
  
  // 토큰 반환
  return response.data;
}

export {
  createSession, createToken,
}