import { styled } from 'styled-components';

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

  /* Dodajmy cień dla wizualnego rozróżnienia */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
`;

export const MessageBubble = styled.div<{ isOwn: boolean }>`
  align-self: ${(props) => (props.isOwn ? 'flex-end' : 'flex-start')};
  background-color: ${(props) => (props.isOwn ? '#dce8ff' : '#ffffff')};
  color: #000;
  padding: 10px 14px;
  border-radius: 14px;
  max-width: 70%;
  line-height: 1.4;

  /* Dodajmy cień i drobne przejście */
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  transition: background-color 0.2s;

  &:hover {
    background-color: ${(props) => (props.isOwn ? '#cbd9ff' : '#f4f4f4')};
  }
`;

export const ChatInputWrapper = styled.div`
  display: flex;
  flex-direction: row;
  gap: 8px;
  align-items: center;

  /* Możemy wstawić cień i tło paska inputu */
  background-color: #fff;
  padding: 8px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
`;
