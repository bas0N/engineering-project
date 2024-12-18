import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { PrimaryButton, TextField, Text, Stack } from "@fluentui/react";
import { ChatInputWrapper, MessagesWrapper, MessageBubble, ChatContainer } from "./ChatDetailPage.styled";
import axios from "axios";
import { ChatService } from "../../../service/ChatService";
import { MessagesResponse, MessageResponse, UserDetailsResponse } from "../../../Chat.types";

const ChatDetailPage: React.FC = () => {
    const { receiverId } = useParams();
    const token = localStorage.getItem('authToken');

    // Przechowujemy aktualnego usera w stanie
    const [currentUserId, setCurrentUserId] = useState<string>('');
    const [messages, setMessages] = useState<MessageResponse[]>([]);
    const [newMessage, setNewMessage] = useState('');
    const [unreadCount, setUnreadCount] = useState<number>(0);

    const [senderInfo, setSenderInfo] = useState<UserDetailsResponse | null>(null);
    const [receiverInfo, setReceiverInfo] = useState<UserDetailsResponse | null>(null);

    const chatServiceRef = useRef<ChatService | null>(null);

    /**
     * 1. useEffect do pobrania currentUserId z backendu.
     *    Robimy to raz – po załadowaniu komponentu.
     */
    useEffect(() => {
        const fetchUserId = async () => {
            if (!token) return;
            try {
                const resp = await axios.get<UserDetailsResponse>(
                    `${import.meta.env.VITE_API_URL}auth/user/details`,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                console.log('User details:', resp.data);
                setCurrentUserId(resp.data.id);
            } catch (err) {
                console.error('Error fetching user details', err);
            }
        };

        fetchUserId();
    }, [token]);

    /**
     * 2. useEffect do głównej logiki chatu (STOMP + loadMessages + unreadCount).
     *    Uruchamiamy dopiero, gdy mamy currentUserId oraz receiverId.
     */
    useEffect(() => {
        // Sprawdzamy, czy posiadamy już currentUserId oraz receiverId
        if (!token || !receiverId || !currentUserId) {
            return;
        }

        console.log('currentUserId:', currentUserId, 'receiverId:', receiverId);

        // Ładujemy istniejące wiadomości z REST:
        loadMessages(token, receiverId);

        // Inicjalizujemy ChatService (STOMP)
        console.log('Activating chat service...');
        chatServiceRef.current = new ChatService({
            token,
            onMessage: (msg: MessageResponse) => {
                // Dodajemy wiadomość do stanu, jeśli dotyczy aktualnej konwersacji
                if (
                    (msg.senderId === receiverId && msg.receiverId === currentUserId) ||
                    (msg.senderId === currentUserId && msg.receiverId === receiverId)
                ) {
                    setMessages(prev => [...prev, msg]);
                }
            },
        });

        chatServiceRef.current.activate();
        console.log('Chat service activated');

        // Pobieramy globalny unreadCount
        fetchGlobalUnreadCount(token);

        return () => {
            console.log('Deactivating chat service...');
            chatServiceRef.current?.deactivate();
        };
    }, [receiverId, currentUserId]);

    /**
     * Pobiera historię wiadomości z backendu, ustawia senderInfo i receiverInfo
     */
    const loadMessages = async (token: string, contactId: string) => {
        try {
            const resp = await axios.get<MessagesResponse>(
                `${import.meta.env.VITE_API_URL}message/${contactId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setMessages(resp.data.messages);
            setSenderInfo(resp.data.sender);
            setReceiverInfo(resp.data.receiver);

            // Oznacz jako przeczytane (opcjonalnie)
            const unreadIds = resp.data.messages
                .filter(m => !m.isRead && m.receiverId === currentUserId)
                .map(m => m.uuid);

            if (unreadIds.length > 0) {
                await axios.post(
                    `${import.meta.env.VITE_API_URL}message/markAsRead`,
                    unreadIds,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
            }
        } catch (err) {
            console.error('Error loading messages', err);
        }
    };

    /**
     * Pobiera globalną liczbę nieprzeczytanych wiadomości
     */
    const fetchGlobalUnreadCount = async (token: string) => {
        try {
            const resp = await axios.get<number>(
                `${import.meta.env.VITE_API_URL}message/unreadCount`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setUnreadCount(resp.data);
        } catch (error) {
            console.error('Error fetching unread count', error);
        }
    };

    /**
     * Wysyłanie wiadomości STOMP
     */
    const handleSendMessage = () => {
        if (!newMessage.trim() || !chatServiceRef.current || !receiverId) {
            return;
        }
        console.log('Sending message:', newMessage);

        chatServiceRef.current.sendMessage(newMessage.trim(), receiverId);
        setNewMessage('');
        // Nie musimy ręcznie dodawać do stanu – bo STOMP i tak wyśle do nas onMessage
    };

    /**
     * Funkcja pomocnicza do wyświetlania nazwy użytkownika
     */
    const getDisplayName = (userId: string) => {
        if (userId === currentUserId) {
            return "You";
        }
        // Zwróć uwagę, że w oryginalnym kodzie było: receiverInfo.userId –
        // upewnij się, że w UserDetailsResponse używasz 'uuid' czy 'id'.
        if (receiverInfo && userId === receiverInfo.id) {
            return receiverInfo.username || receiverInfo.email || "Unknown user";
        }
        if (senderInfo && userId === senderInfo.id) {
            return senderInfo.username || senderInfo.email || "Unknown user";
        }
        return "Unknown user";
    };

    return (
        <ChatContainer>
            <Stack style={{ padding: 16 }}>
                <Text variant="large">
                    Chat with {receiverInfo?.username || receiverInfo?.email || receiverId}
                </Text>
                <Text variant="small">Global unread count: {unreadCount}</Text>

                <MessagesWrapper>
                    {messages.map((msg) => {
                        const isOwn = (msg.senderId === currentUserId);
                        const displayName = getDisplayName(msg.senderId);

                        return (
                            <MessageBubble key={msg.uuid} isOwn={isOwn}>
                                <strong>{displayName}</strong>: {msg.content}
                                <div style={{ fontSize: '0.8em', marginTop: 4 }}>
                                    {msg.dateAdded}
                                    {!msg.isRead && ' (unread)'}
                                </div>
                            </MessageBubble>
                        );
                    })}
                </MessagesWrapper>

                <ChatInputWrapper>
                    <TextField
                        value={newMessage}
                        onChange={(_, val) => setNewMessage(val || '')}
                        placeholder="Enter message..."
                        styles={{
                            fieldGroup: { backgroundColor: '#fff' },
                            root: { flex: 1, marginRight: 8 },
                        }}
                    />
                    <PrimaryButton text="Send" onClick={handleSendMessage} />
                </ChatInputWrapper>
            </Stack>
        </ChatContainer>
    );
};

export default ChatDetailPage;
