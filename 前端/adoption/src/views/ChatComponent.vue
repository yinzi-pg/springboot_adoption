<template>
  <div class="chat-container">
    <!-- 对话标题（与整体布局风格统一） -->
    <div class="chat-header">
      <el-icon class="header-icon"><Crop /></el-icon>
      <span class="header-title">文心对话助手</span>
    </div>

    <!-- 消息列表（适应红框高度，超出滚动） -->
    <div class="chat-history" ref="chatHistory">
      <!-- 空状态提示 -->
      <div v-if="messages.length === 0" class="empty-history">
        <el-icon class="empty-icon"><Message /></el-icon>
        <p class="empty-text">开始与AI助手对话吧～</p>
      </div>

      <!-- 消息项 -->
      <div 
        v-for="(message, index) in messages" 
        :key="index" 
        :class="['message-item', message.isUser ? 'user-message' : 'ai-message']"
      >
        <!-- 头像 -->
        <el-avatar 
          class="message-avatar" 
          :src="message.isUser ? userAvatar : aiAvatar"
          size="small"
        >
          {{ message.isUser ? '我' : 'AI' }}
        </el-avatar>

        <!-- 消息内容 -->
        <div class="message-content">
          <p class="content-text">{{ message.content }}</p>
          <!-- 消息时间（可选） -->
          <span class="content-time">{{ formatTime(message.timestamp) }}</span>
        </div>
      </div>

      <!-- 加载中指示器 -->
      <div v-if="isLoading" class="loading-wrapper">
        <el-skeleton loading :rows="1" width="60%" class="loading-skeleton"></el-skeleton>
        <el-skeleton loading :rows="1" width="40%" class="loading-skeleton"></el-skeleton>
      </div>
    </div>

    <!-- 输入区域（固定在组件底部，不溢出红框） -->
    <div class="chat-input">
      <el-input
        v-model="userInput"
        type="textarea"
        :rows="1"
        :disabled="isLoading"
        placeholder="请输入消息..."
        @keydown.enter.prevent="sendMessage"
        @focus="inputFocused = true"
        @blur="inputFocused = false"
        class="input-area"
        resize="none"
      ></el-input>
      <el-button 
        type="primary" 
        :disabled="!userInput.trim() || isLoading" 
        @click="sendMessage"
        class="send-btn"
        :icon="isLoading ? Loading : Send"
      >
        {{ isLoading ? '发送中' : '发送' }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { Crop, Message, Loading} from '@element-plus/icons-vue';
import { ElAvatar, ElIcon, ElSkeleton, ElMessage } from 'element-plus';
import chatService from '@/api/chat.js'; // 复用之前的请求服务
import { useUserInfoStore } from '@/stores/user.js'; // 可选：若需用户头像

// 状态管理
const messages = ref([
  { 
    content: "你好！我是Deepseek的AI助手，可帮你解答问题、处理需求～", 
    isUser: false, 
    timestamp: Date.now() 
  }
]);
const userInput = ref('');
const isLoading = ref(false);
const inputFocused = ref(false);
const chatHistory = ref(null);

// 头像配置（适配整体布局的默认头像）
const userInfoStore = useUserInfoStore();
const userAvatar = ref(userInfoStore.info.avatar || "https://picsum.photos/id/1005/40/40");
const aiAvatar = "https://picsum.photos/id/1012/40/40";

// 格式化消息时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp);
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
};

// 发送消息（逻辑与之前一致，适配后端返回格式）
const sendMessage = async () => {
      const content = userInput.value.trim();
      if (!content) return;

      // 添加用户消息到历史
      const userMsg = { content, isUser: true, timestamp: Date.now() };
      messages.value.push(userMsg);
      userInput.value = '';
      isLoading.value = true;

      try {
        // 调用后端API
        const response = await chatService.sendMessage(content);
        
        // 关键：先判断response是否存在
        if (!response) {
          throw new Error("未收到服务器响应");
        }

        if (response.success) {
          // 添加AI回复
          messages.value.push({ 
            content: response.reply, 
            isUser: false, 
            timestamp: Date.now() 
          });
        } else {
          // 后端返回业务错误（如余额不足）
          throw new Error(response.message || "AI回复获取失败");
        }
      } catch (error) {
        // 统一处理所有错误（网络错误、业务错误等）
        const errorMsg = error.message || "对话失败，请稍后再试";
        messages.value.push({ 
          content: `错误：${errorMsg}`, 
          isUser: false, 
          timestamp: Date.now() 
        });
        ElMessage({ type: 'error', message: errorMsg });
        console.error("Chat error:", error);
      } finally {
        isLoading.value = false;
        await nextTick();
        scrollToBottom();
      }
    };

// 滚动到最新消息（适配红框高度）
const scrollToBottom = () => {
  if (chatHistory.value) {
    chatHistory.value.scrollTop = chatHistory.value.scrollHeight;
  }
};

// 初始化：滚动到底部 + 输入框聚焦
onMounted(() => {
  scrollToBottom();
  // 延迟聚焦，确保组件渲染完成
  setTimeout(() => {
    document.querySelector('.input-area')?.focus();
  }, 300);

  // 窗口大小变化时重新调整滚动（适配红框 resize 场景）
  window.addEventListener('resize', scrollToBottom);
  return () => window.removeEventListener('resize', scrollToBottom);
});
</script>

<style lang="scss" scoped>
/* 核心：完全适配红框的100%高度和宽度 */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%; /* 继承父容器（红框）的570px高度 */
  width: 100%;  /* 继承父容器宽度 */
  margin: 0;
  padding: 12px;
  box-sizing: border-box;
  background-color: #fff; /* 与el-main背景统一 */
}

/* 对话标题（呼应左侧蓝色菜单） */
.chat-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;

  .header-icon {
    color: #309bcc; /* 与左侧菜单蓝色一致 */
    font-size: 18px;
  }

  .header-title {
    font-size: 16px;
    font-weight: 500;
    color: #333;
  }
}

/* 消息列表：占据红框剩余高度，超出滚动 */
.chat-history {
  flex: 1; /* 关键：填充中间剩余空间 */
  overflow-y: auto;
  padding: 15px;
  background-color: #f9fafb;
  border-radius: 8px;
  margin-bottom: 12px;
  scroll-behavior: smooth; /* 平滑滚动 */

  /* 滚动条美化（适配整体风格） */
  &::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }
  &::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 3px;
  }
  &::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 3px;
    &:hover {
      background: #94a3b8;
    }
  }
}

/* 空消息状态 */
.empty-history {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #94a3b8;

  .empty-icon {
    font-size: 32px;
    margin-bottom: 8px;
  }

  .empty-text {
    font-size: 14px;
  }
}

/* 消息项布局 */
.message-item {
  display: flex;
  margin-bottom: 14px;
  max-width: 80%; /* 限制消息宽度，避免过长 */
  animation: fadeIn 0.3s ease-in-out; /* 消息淡入动画 */
}

/* 淡入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 用户消息：右对齐 */
.user-message {
  flex-direction: row-reverse;
  margin-left: auto; /* 右靠齐 */
}

/* 头像样式（与整体布局的头像统一） */
.message-avatar {
  margin: 0 8px;
  flex-shrink: 0; /* 防止头像被压缩 */
  .el-avatar {
    width: 36px;
    height: 36px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08);
  }
}

/* 用户消息头像：调整间距 */
.user-message .message-avatar {
  margin-left: 0;
  margin-right: 8px;
}

/* 消息内容气泡 */
.message-content {
  position: relative;
  padding: 10px 14px;
  border-radius: 18px;
  line-height: 1.6;
  font-size: 14px;
  word-break: break-word; /* 长文本换行 */

  /* 消息时间 */
  .content-time {
    display: block;
    text-align: right;
    font-size: 12px;
    margin-top: 4px;
    color: #94a3b8;
  }
}

/* 用户消息气泡（蓝色系，与左侧菜单呼应） */
.user-message .message-content {
  background-color: #309bcc;
  color: #fff;
  border-top-right-radius: 4px; /* 右上角圆角减小，更自然 */
}

/* AI消息气泡（浅灰色，与整体风格协调） */
.ai-message .message-content {
  background-color: #f1f5f9;
  color: #334155;
  border-top-left-radius: 4px; /* 左上角圆角减小 */
}

/* 加载中骨架屏 */
.loading-wrapper {
  margin-left: 48px; /* 与AI消息头像对齐 */
  margin-bottom: 14px;
}

.loading-skeleton {
  margin-bottom: 6px;
  border-radius: 8px;
}

/* 输入区域（适配红框底部，不溢出） */
.chat-input {
  display: flex;
  gap: 8px;
  align-items: flex-end; /* 输入框与按钮底部对齐 */
}

/* 输入框样式（与Element Plus输入框风格统一） */
.input-area {
  flex: 1;
  min-height: 44px;
  max-height: 120px; /* 限制输入框最大高度 */
  padding: 10px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 22px; /* 圆角与按钮统一 */
  font-size: 14px;
  transition: all 0.2s ease;

  &:focus {
    border-color: #309bcc; /* 聚焦时蓝色边框，与整体风格统一 */
    box-shadow: 0 0 0 2px rgba(48, 155, 204, 0.1);
    outline: none;
  }

  &:disabled {
    background-color: #f9fafb;
    cursor: not-allowed;
  }
}

/* 发送按钮（蓝色系，与左侧菜单统一） */
.send-btn {
  height: 44px;
  padding: 0 20px;
  border-radius: 22px; /* 圆角优化 */
  background-color: #309bcc;
  border-color: #309bcc;

  &:hover:not(:disabled) {
    background-color: #2583b9; /* hover加深蓝色 */
    border-color: #2583b9;
  }

  &:disabled {
    background-color: #cbd5e1;
    border-color: #cbd5e1;
  }
}

/* 适配红框高度异常场景（兜底） */
@media (max-height: 500px) {
  .chat-header {
    margin-bottom: 6px;
    padding-bottom: 4px;
  }

  .chat-history {
    margin-bottom: 8px;
    padding: 10px;
  }

  .message-item {
    margin-bottom: 10px;
    max-width: 85%;
  }

  .send-btn {
    height: 40px;
    padding: 0 16px;
  }
}
</style>