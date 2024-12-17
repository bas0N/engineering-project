// src/chat/ChatListPage/ChatListPage.styled.ts
import { styled } from 'styled-components';

export const ListWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin: 16px;
`;

export const ChatItemContainer = styled.div<{ isRead: boolean }>`
  background-color: ${(props) => (props.isRead ? '#f2f2f2' : '#e0e0e0')};
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  &:hover {
    background-color: #cfcfcf;
  }
`;

export const ChatItemHeader = styled.div`
  font-weight: bold;
  margin-bottom: 4px;
`;

export const ChatItemSubtext = styled.div`
  font-size: 0.9em;
  color: #555;
`;
