import { styled } from "styled-components";
import {Button, Input } from '@fluentui/react-components';


export const ChatDetailContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  height: 90vh; 
  border: 1px solid #ccc;
  border-radius: 4px;
`;

export const ChatHeader = styled.div`
  padding: 10px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: space-between;
`;


export const MessagesContainer = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  background-color: #fafafa;
`;


export const MessageBubble = styled.div<{ isOwn?: boolean }>`
  align-self: ${(props) => (props.isOwn ? 'flex-end' : 'flex-start')};
  background-color: ${(props) => (props.isOwn ? '#0078D4' : '#e1dfdd')};
  color: ${(props) => (props.isOwn ? '#fff' : '#000')};
  padding: 8px 12px;
  border-radius: 12px;
  max-width: 60%;
`;


export const ChatInputContainer = styled.div`
  display: flex;
  align-items: center;
  padding: 10px;
  gap: 8px;
  border-top: 1px solid #eee;
`;

export const ChatTextInput = styled(Input)`
  flex: 1;
`;

export const SendButton = styled(Button)`
  flex-shrink: 0;
`;
