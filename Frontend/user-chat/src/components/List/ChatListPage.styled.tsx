import { styled } from "styled-components";
import { Text, Card, CardHeader, CardFooter } from '@fluentui/react-components';

export const ChatListContainer = styled.div`
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: inherit;
`;

export const ChatItemCard = styled(Card)`
  margin-bottom: 10px;
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.3s ease;
  &:hover {
    background-color: #f3f2f1;
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  &:active {
    transform: scale(0.99);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.15);
  }
`;

export const ChatHeader = styled(CardHeader)`
  padding: 12px 16px;
  border-bottom: 1px solid #e1dfdd;
`;

export const ChatFooter = styled(CardFooter)`
  padding: 12px 16px;
  border-top: 1px solid #e1dfdd;
  text-align: right;
`;

export const ChatTitle = styled(Text)`
  margin-bottom: 4px;
  font-weight: 600;
  font-size: 1rem; 
  color: inherit;
`;

export const ChatSubtitle = styled(Text)`
  font-size: 0.875rem; 
  color: #606060;
`;

export const NoChatsText = styled(Text)`
  display: block;
  margin: 20px 0;
  text-align: center;
  font-size: 1rem; /* Adjust based on size scale */
  color: #8a8886;
`;
