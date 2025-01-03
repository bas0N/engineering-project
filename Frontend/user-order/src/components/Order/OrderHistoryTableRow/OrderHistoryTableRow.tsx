import { TableCell } from "@fluentui/react-components";
import { ItemResponse } from "../Order.types";
import { OrderHistoryTableRowWrapper, OrderItemsImage } from "./OrderHistoryTableRow.styled";

interface OrderHistoryTableRowProps {
    bgColor: string;
    item: ItemResponse;
}

export const OrderHistoryTableRow = ({
    bgColor,
    item
} : OrderHistoryTableRowProps) => (
    <OrderHistoryTableRowWrapper
        bgColor={bgColor}
    >
        <TableCell>{item.name}</TableCell>
        <TableCell>
            <OrderItemsImage
                src={item.imageUrl}
                alt={item.name}
            />
        </TableCell>
        <TableCell>{item.quantity}</TableCell>
        <TableCell>{item.priceUnit} PLN</TableCell>
        <TableCell>{item.priceSummary} PLN</TableCell>
    </OrderHistoryTableRowWrapper>
)