import { Text } from "@fluentui/react-components";
import { OrderLabel, OrderSummaryContainer } from "./OrderSummary.styled";

interface OrderSummaryProps {
    totalPrice: number;
    summaryLabel: string;
    totalLabel: string;
}

export const OrderSummary = ({ totalPrice, summaryLabel, totalLabel }: OrderSummaryProps) => {
    return (
        <OrderSummaryContainer>
            <Text>{summaryLabel}</Text>
            <OrderLabel>{totalLabel}: {totalPrice} PLN</OrderLabel>
        </OrderSummaryContainer>
    );
}