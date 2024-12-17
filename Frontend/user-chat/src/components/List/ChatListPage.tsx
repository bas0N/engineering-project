import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Stack, Text } from '@fluentui/react';
import { ChatItemContainer, ChatItemHeader, ChatItemSubtext, ListWrapper } from './ChatListPage.styled';
import { ChatResponse } from '../../Chat.types';
import { ChatService } from '../../service/ChatService';

const ChatListPage = () => {
    const [chats, setChats] = useState<ChatResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [unreadCount, setUnreadCount] = useState<number>(0);
    const navigate = useNavigate();

    useEffect(() => {
        const loadData = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem('authToken');
                if (!token) {
                    throw new Error('No token found');
                }

                const chatsData = await ChatService.fetchChats(token);
                setChats(chatsData);

                const count = await ChatService.fetchUnreadCount(token);
                setUnreadCount(count);

            } catch (err) {
                console.error('Error loading chats', err);
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, []);

    const handleChatClick = (chat: ChatResponse) => {
        navigate(`/chat-detail/${chat.receiverId}`);
    };

    return (
        <Stack>
            <Text variant="xLarge" style={{ margin: 16 }}>Conversations</Text>
            {loading && <Text>Loading...</Text>}

            {!loading && (
                <div style={{ marginLeft: 16, marginBottom: 16 }}>
                    <Text variant="mediumPlus">
                        Total unread messages: {unreadCount}
                    </Text>
                </div>
            )}

            <ListWrapper>
                {!loading && chats.map((chat, index) => (
                    <ChatItemContainer
                        key={index}
                        isRead={chat.isRead}
                        onClick={() => handleChatClick(chat)}
                    >
                        <ChatItemHeader>{chat.username}</ChatItemHeader>
                        <ChatItemSubtext>{chat.lastMessage}</ChatItemSubtext>
                        <ChatItemSubtext>
                            {chat.lastMessageTime}
                            {chat.unreadCount > 0 && ` • New Messages: ${chat.unreadCount}`}
                        </ChatItemSubtext>
                    </ChatItemContainer>
                ))}
            </ListWrapper>
        </Stack>
    );
};

export default ChatListPage;
