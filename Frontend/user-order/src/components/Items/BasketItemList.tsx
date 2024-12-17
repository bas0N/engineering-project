import { BasketItem } from "../../Order.types";
import { Text } from "@fluentui/react-components";
import {useBasketItemsListStyles} from "./BaksteItemList.styled";


interface BasketItemsListProps {
    items: BasketItem[];
}

export function BasketItemsList({ items }: BasketItemsListProps) {
    const styles = useBasketItemsListStyles();

    return (
        <div className={styles.container}>
            <Text className={styles.header}>Items in your basket:</Text>
            {items.map(item => (
                <div key={item.uuid} className={styles.item}>
                    <img src={item.imageUrl} alt={item.name} className={styles.image}/>
                    <div className={styles.details}>
                        <Text>{item.name}</Text>
                        <Text>Quantity: {item.quantity}</Text>
                        <Text>Price: {item.summaryPrice} PLN</Text>
                    </div>
                </div>
            ))}
        </div>
    );
}
