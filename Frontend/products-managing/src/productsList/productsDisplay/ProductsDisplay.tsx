import { 
    createTableColumn, 
    TableColumnDefinition,
    TableCellLayout,
    DataGridHeader,
    DataGridRow,
    DataGridHeaderCell,
    DataGridBody,
    DataGridCell,
    Text,
    Button,
    DataGridProps,
    TableRowId,
} from "@fluentui/react-components";
import { useNavigate } from 'react-router-dom';
import { useTranslation } from "react-i18next";
import { 
    ProductsDisplayWrapper, 
    ProductActionsWrapper 
} from "./ProductsDisplay.styled";
import { useState } from "react";

export type Product = {
    parentAsin: string;
    categories: string[];
    mainCategory: string;
    details: Record<string, string>;
    features: string[];
    description: string[];
    price: string;
    store: string;
    title: string;
    ratingNumber: number;
    averageRating: number;
};

export interface ProductsDisplayProps {
    products: Product[];
    deleteProduct: (productId: string) => void;
}

export const ProductsDisplay = ({
    products,
    deleteProduct,
} : ProductsDisplayProps) => {

    const {t} = useTranslation();

    const navigate = useNavigate();
    const [selectedProducts, setSelectedProducts] = useState(
        new Set<TableRowId>([])
    );
    const onSelectionChange: DataGridProps["onSelectionChange"] = (_e, data) => {
        setSelectedProducts(data.selectedItems);
    };

    const columns: TableColumnDefinition<Product>[] = [
        createTableColumn<Product>({
            columnId: "title",
            compare: (a, b) => a.title.localeCompare(b.title),
            renderHeaderCell: () => t('productsList.title'),
            renderCell: (item) =>  (
                <TableCellLayout>
                  {item.title}
                </TableCellLayout>
            )
        }),
        createTableColumn<Product>({
            columnId: "ratingNumber",
            compare: (a, b) => a.ratingNumber > b.ratingNumber ? 1 : a.ratingNumber < b.ratingNumber ? -1 : 0,
            renderHeaderCell: () => t('productsList.numberOfRatings'),
            renderCell: (item) =>  (
                <TableCellLayout>
                    <Text>
                        {item.ratingNumber}
                    </Text>
                </TableCellLayout>
            )
        }),
        createTableColumn<Product>({
            columnId: "averageRating",
            compare: (a, b) => a.averageRating > b.averageRating ? 1 : a.averageRating < b.averageRating ? -1 : 0,
            renderHeaderCell: () => t('productsList.averageRating'),
            renderCell: (item) =>  (
                <TableCellLayout>
                    <Text align='center'>
                        {item.averageRating}
                    </Text>
                </TableCellLayout>
            )
        }),
        createTableColumn<Product>({
            columnId: "store",
            compare: (a, b) => a.store.localeCompare(b.store),
            renderHeaderCell: () => t('productsList.store'),
            renderCell: (item) =>  (
                <TableCellLayout>
                    <Text align='center'>
                        {item.store}
                    </Text>
                </TableCellLayout>
            )
        }),
        createTableColumn<Product>({
            columnId: "price",
            compare: (a, b) => {
                const priceA = Number(a.price);
                const priceB = Number(b.price);
    
                return priceA > priceB ? 1 : priceA < priceB ? -1 : 0;
            },
            renderHeaderCell: () => t('productsList.price'),
            renderCell: (item) =>  (
                <TableCellLayout>
                    <Text align='center'>
                        {item.price}
                    </Text>
                </TableCellLayout>
            )
        })
    ]

    const selectedProductId = ((products as Product[]).find((product) => product.parentAsin === selectedProducts.values().next().value as string) as Product)?.parentAsin ?? '';

    return (<>
        <ProductsDisplayWrapper
            columns={columns}
            items={products}
            sortable
            selectionMode="single"
            getRowId={(item: Product) => item.parentAsin}
            resizableColumnsOptions={{
                autoFitColumns: true,
            }}
            selectedItems={selectedProducts}
            onSelectionChange={onSelectionChange}
        >
            <DataGridHeader>
                <DataGridRow selectionCell={{ radioIndicator: { "aria-label": "Select row" } }}>
                    {({ renderHeaderCell }) => (
                        <DataGridHeaderCell>{renderHeaderCell()}</DataGridHeaderCell>
                    )}
                </DataGridRow>
            </DataGridHeader>
            <DataGridBody<Product>>
                {({ item, rowId }) => (
                    <DataGridRow<Product>
                        key={rowId}
                        selectionCell={{ radioIndicator: { "aria-label": "Select row" } }}
                    >
                        {({ renderCell }) => (
                            <DataGridCell>
                                {renderCell(item)}
                            </DataGridCell>
                        )}
                    </DataGridRow>
                )}
            </DataGridBody>
        </ProductsDisplayWrapper>

        <ProductActionsWrapper>
            <Button
                disabled={selectedProductId === ''} 
                onClick={() => navigate(`/product/${selectedProductId}`)}>
                {t('productsList.actions.checkout')}
            </Button>
            <Button
                disabled={selectedProductId === ''} 
                onClick={() => deleteProduct(selectedProductId)}
            >
                {t('productsList.actions.delete')}
            </Button>
        </ProductActionsWrapper>
    </>);
};