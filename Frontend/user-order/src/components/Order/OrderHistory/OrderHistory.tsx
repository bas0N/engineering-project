import {useEffect, useState} from 'react';
import axios from 'axios';
import {ItemResponse} from '../Order.types';
import {
    Spinner,
    Text,
    TableCell,
    TableBody,
    TableHeader,
    TableRow,
    Divider,
} from "@fluentui/react-components";
import {
    OrderHistoryContainer,
    OrderHistoryTable,
    OrderHistoryHeader,
    OrderHistoryCard,
    OrderHistoryCardHeader,
    OrderHistoryFooter,
    OrderHistoryCardContent,
} from "./OrderHistory.styled";
import { useTranslation } from 'react-i18next';
import { OrderHistoryTableRow } from '../OrderHistoryTableRow/OrderHistoryTableRow';

interface Order {
    orderId: string;
    status: string;
    items: ItemResponse[];
    summaryPrice: number;
}

export default function OrderHistory() {
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const {t} = useTranslation();
    const token = localStorage.getItem('authToken');

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                setError(false)
                const response = await axios.get<Order[]>(`${import.meta.env.VITE_API_URL}order`, {
                    headers: {'Authorization': `Bearer ${token}`}
                });
                setOrders(response.data);
                setLoading(false);
            } catch {
                setError(true);
                setLoading(false);
            }
        };
        fetchOrders();
    }, [token]);

    if (loading) {
        return <Spinner label={t('orderHistory.loading')}/>;
    }

    if (error) {
        return <Text style={{color: "red"}}>{t('orderHistory.loadingError')}</Text>;
    }

    return (
        <OrderHistoryContainer>
            <OrderHistoryHeader align='center'>{t('orderHistory.title')}</OrderHistoryHeader>
            {orders.length === 0 ? (
                <Text>{t('orderHistory.noOrders')}</Text>
            ) : (
                orders.map((order) => (
                    <OrderHistoryCard key={order.orderId}>
                        <OrderHistoryCardHeader>{t('orderHistory.orderId')}: {order.orderId}</OrderHistoryCardHeader>
                        <Divider />
                        <OrderHistoryCardContent>
                            <Text weight='bold'>
                                {t('orderHistory.status')}: {order.status}
                            </Text>
                            <OrderHistoryTable aria-label="Order Items">
                                <TableHeader>
                                    <TableRow>
                                        <TableCell>{t('orderHistory.name')}</TableCell>
                                        <TableCell>{t('orderHistory.image')}</TableCell>
                                        <TableCell>{t('orderHistory.quantity')}</TableCell>
                                        <TableCell>{t('orderHistory.priceUnit')}</TableCell>
                                        <TableCell>{t('orderHistory.priceSummary')}</TableCell>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {order.items.map((item, index) => (
                                        <OrderHistoryTableRow
                                            key={item.uuid}
                                            bgColor={index % 2 === 0 ? '#1f1f1f' : '#292929'}
                                            item={item}
                                        />
                                    ))}
                                </TableBody>
                            </OrderHistoryTable>
                        </OrderHistoryCardContent>
                        <OrderHistoryFooter>
                            {t('orderHistory.totalPrice')}: {order.summaryPrice} PLN
                        </OrderHistoryFooter>
                    </OrderHistoryCard>
                ))
            )}
        </OrderHistoryContainer>
    );
}
