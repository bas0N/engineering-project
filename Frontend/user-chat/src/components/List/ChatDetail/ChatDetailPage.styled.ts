// ChatDetailPage.styled.ts
import { styled } from 'styled-components';

export const ChatContainer = styled.div`
  max-width: 700px;
  margin: 0 auto; 
  background-color: #f0f0f0; /
  min-height: 80vh; 
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
`;

export const MessagesWrapper = styled.div`
  background-color: #f5f5f5;
  padding: 8px;
  height: 60vh;
  overflow-y: auto;
  margin: 16px 0;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;

  box-shadow: inset 0 1px 2px rgba(0,0,0,0.1); 
`;

export const MessageBubble = styled.div.withConfig({
    shouldForwardProp: (prop) => prop !== 'isOwn'
})<{ isOwn: boolean }>`
  align-self: ${(props) => (props.isOwn ? 'flex-end' : 'flex-start')};
  background-color: ${(props) => (props.isOwn ? '#0b93f6' : '#3a3a3a')};
  color: #fff;
  padding: 10px 14px;
  border-radius: 14px;
  max-width: 70%;
  line-height: 1.4;
  font-size: 0.95rem;
  margin-bottom: 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);

  &:hover {
    background-color: ${(props) => (props.isOwn ? '#0877c2' : '#333')};
  }
`;
export const ChatInputWrapper = styled.div`
  display: flex;
  flex-direction: row;
  gap: 8px;
  align-items: center;
  background-color: #fff;
  padding: 8px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
`;
