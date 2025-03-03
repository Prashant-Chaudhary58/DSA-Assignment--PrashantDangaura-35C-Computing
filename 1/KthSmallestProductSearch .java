class KthSmallestProductSearch {
    public static long findKthSmallestProduct(int[] arr1, int[] arr2, long k) {
        long left = (long) arr1[0] * arr2[0];
        long right = (long) arr1[arr1.length - 1] * arr2[arr2.length - 1];

        while (left < right) {
            long mid = left + (right - left) / 2;
            if (countProductsLessThanOrEqual(arr1, arr2, mid) < k) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    private static long countProductsLessThanOrEqual(int[] arr1, int[] arr2, long target) {
        long count = 0;
        for (int num1 : arr1) {
            int low = 0;
            int high = arr2.length;
            while (low < high) {
                int mid = low + (high - low) / 2;
                if ((long) num1 * arr2[mid] <= target) {
                    low = mid + 1;
                } else {
                    high = mid;
                }
            }
            count += low;
        }
        return count;
    }

    public static void main(String[] args) {
        int[] arr1 = {2, 5};
        int[] arr2 = {3, 4};
        long k = 2;
        System.out.println(findKthSmallestProduct(arr1, arr2, k)); // Output: 8

        int[] arr1_2 = {-4, -2, 0, 3};
        int[] arr2_2 = {2, 4};
        long k2 = 6;
        System.out.println(findKthSmallestProduct(arr1_2, arr2_2, k2)); // Output: 0

        int[] arr1_3 = {-4,-2,0,3};
        int[] arr2_3 = {-2,4};
        long k3 = 3;
        System.out.println(findKthSmallestProduct(arr1_3, arr2_3, k3)); //output: -8

        int[] arr1_4 = {100000};
        int[] arr2_4 = {100000};
        long k4 = 1;
        System.out.println(findKthSmallestProduct(arr1_4,arr2_4,k4)); //output: 10000000000
    }
}