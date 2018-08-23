import io.functional.Function;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static io.functional.CollectionUtilities.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class CollectionUtilitiesTest {

    @Test
    public void empty_list() {
        assertThat(list()).hasSize(0);
    }

    @Test
    public void single_item_list() {
        List<String> singleItemList = list("item");
        assertThat(singleItemList).hasSize(1);
        assertThat(singleItemList.get(0)).isEqualTo("item");
    }

    @Test
    public void list_from_list() {
        List<String> listFromList = list(list("item", "item2"));
        assertThat(listFromList).hasSize(2);
        assertThat(listFromList.get(0)).isEqualTo("item");
        assertThat(listFromList.get(1)).isEqualTo("item2");
    }

    @Test
    public void vararg_list() {
        List<String> varargList = list("item", "item2");
        assertThat(varargList).hasSize(2);
        assertThat(varargList.get(0)).isEqualTo("item");
        assertThat(varargList.get(1)).isEqualTo("item2");
    }

    @Test
    public void head_list() {
        String result = head(list("item", "item2"));
        assertThat(result).isEqualTo("item");
    }

    @Test(expected = IllegalStateException.class)
    public void head_of_empty_list() {
        head(list());
    }

    @Test
    public void tail_list() {
        List<String> result = tail(list("item", "item2", "item3"));
        assertThat(result).isEqualTo(list("item2", "item3"));
    }

    @Test(expected = IllegalStateException.class)
    public void tail_of_empty_list() {
        tail(list());
    }

    @Test
    public void append_to_list() {
        List<String> result = append(list("item", "item2"), "item3");
        assertThat(result).isEqualTo(list("item", "item2", "item3"));
    }

    @Test
    public void append_to_null_list() {
        List<String> result = append(null, "item3");
        assertThat(result).hasSameElementsAs(list("item3"));
    }

    @Test
    public void sum_via_list_fold() {
        Long result = foldLeft(list(1, 2, 3), 1L, x -> y -> x * y);

        assertThat(result).isEqualTo(6L);
    }

    @Test
    public void left_fold_brackets() {
        List<Integer> list = list(1, 2, 3, 4, 5);
        String identity = "0";

        String result = foldLeft(list, identity, x -> y -> addSI(x, y));

        assertThat(result).isEqualTo("(((((0 + 1) + 2) + 3) + 4) + 5)");
    }

    private String addSI(String s, Integer i) {
        return "(" + s + " + " + i + ")";
    }

    @Test
    public void right_fold_brackets() {
        List<Integer> list = list(1, 2, 3, 4, 5);
        String identity = "0";

        String result = foldRight(list, identity, x -> y -> addIS(x, y));

        assertThat(result).isEqualTo("(1 + (2 + (3 + (4 + (5 + 0)))))");
    }

    private String addIS(Integer i, String s) {
        return "(" + i + " + " + s + ")";
    }

    @Test
    public void reverse_list() {
        List<Integer> reversed = reverse(list(1, 2, 3));

        assertThat(reversed).isEqualTo(list(3, 2, 1));
    }

    @SuppressWarnings("all")
    @Parameters(method = "lists")
    @Test(expected = UnsupportedOperationException.class)
    public void empty_list_immutability(List<Object> list) {
        list.add(new Object());
    }

    @SuppressWarnings("unused")
    private Object lists() {
        return new Object[] {
                list(),
                list(new Object()),
                list(new Object(), new Object()),
                list(list(new Object()))
        };
    }
}
