import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import {
    ChatListContainer,
    ChatItemCard,
    ChatHeader,
    ChatFooter,
    ChatTitle,
    ChatSubtitle,
    NoChatsText,
} from './ChatListPage.styled';
import { Text } from '@fluentui/react-components';
import { ChatResponse } from '../../Chat.types.ts';

export const ChatListPage = () => {
    const [chats, setChats] = useState<ChatResponse[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>('');
    const navigate = useNavigate();
    const token = localStorage.getItem('authToken');

    useEffect(() => {
        const fetchChats = async () => {
            setLoading(true);


            try {
                const response = await axios.get(`${import.meta.env.VITE_API_URL}message/chats`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    }
                });

                setChats(response.data);
            } catch (err: any) {
                setError(err.response?.data?.message || 'Error fetching chats');
            } finally {
                setLoading(false);
            }
        };

        fetchChats();
    }, []);

    const handleChatClick = (receiverId: string) => {
        navigate(`/chat/${receiverId}`);
    };

    return (
        <ChatListContainer>
            <Text size={600} weight="semibold" block>
                Your Chats
            </Text>
            {loading && <Text>Loading chats...</Text>}
            {error && <Text>{error}</Text>}

            {!loading && !error && chats.length === 0 && (
                <NoChatsText>No chats found.</NoChatsText>
            )}

            {chats.map((chat) => {
                const displayName = chat.username?.trim() || chat.email || 'Unknown User';
                const lastMsg = chat.lastMessage || 'No message yet';

                return (
                    <ChatItemCard
                        key={chat.receiverId}
                        onClick={() => handleChatClick(chat.receiverId)}
                    >
                        <ChatHeader
                            header={<ChatTitle>{displayName}</ChatTitle>}
                            description={<ChatSubtitle>{`Unread: ${chat.unreadCount}`}</ChatSubtitle>}
                        />
                        <ChatFooter>
                            <Text size={300} block>
                                Last: {lastMsg.substring(0, 50)}
                            </Text>
                            <Text size={200} block>
                                {new Date(chat.lastMessageTime).toLocaleString()}
                            </Text>
                        </ChatFooter>
                    </ChatItemCard>
                );
            })}
        </ChatListContainer>
    );
};
