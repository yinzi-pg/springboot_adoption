import  request  from '@/utils/request.js'

const chatService = {
  // 发送消息到后端
  sendMessage: async (message) => {
    const response = await request.post('/api/chat', {
      content: message
    });
    return response;
  },
  
  // 获取历史对话
  getHistory: async () => {
    const response = await request.get('/api/chat/history');
    return response;
  }
};

export default chatService;