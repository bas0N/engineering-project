import { styled } from 'styled-components';

export const ListWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 12px; /* More spacing between chat items */
  margin: 16px;
`;

export const ChatItemContainer = styled.div<{ isRead: boolean }>`
  display: flex;
  flex-direction: column;
  background-color: ${(props) => (props.isRead ? '#f9f9f9' : '#fff')};
  padding: 16px; /* Increased padding */
  border-radius: 8px; /* Rounded corners */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Soft shadow */
  cursor: pointer;
  transition: background-color 0.2s ease-in-out, box-shadow 0.2s ease-in-out;

  &:hover {
    background-color: #f1f1f1;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  }
`;

export const ChatItemHeader = styled.div<{ isRead: boolean }>`
  font-weight: ${(props) => (props.isRead ? 'normal' : 'bold')}; /* Bold for unread */
  color: #333; /* Darker text for better readability */
  font-size: 1.1em;
`;

export const ChatItemSubtext = styled.div`
  font-size: 0.9em;
  color: #666;
  margin-top: 4px;
`;

export const UnreadDot = styled.span`
  width: 10px;
  height: 10px;
  background-color: #ff4d4f; /* Red dot for unread messages */
  border-radius: 50%;
  margin-left: 8px;
`;
