    <script src="/assets/js/common.js"></script>
    <script src="/assets/js/dropdown.js"></script>
    <?php if (isset($pageJS)): ?>
        <?php foreach ((array)$pageJS as $js): ?>
            <script src="/assets/js/<?= $js ?>.js"></script>
        <?php endforeach; ?>
    <?php endif; ?>
</body>
</html>

